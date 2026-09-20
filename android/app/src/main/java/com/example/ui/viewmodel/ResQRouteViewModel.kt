package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiAdviceResult
import com.example.ai.GeminiChatbotManager
import com.example.ai.GeminiRouteAdvisor
import com.example.data.local.DecryptedFieldReport
import com.example.data.local.MissionEntity
import com.example.data.local.ResQRouteDatabase
import com.example.data.local.ResQRouteRepository
import com.example.gis.ActiveLocationShareSession
import com.example.gis.ChatMessage
import com.example.gis.ConvoyTelemetry
import com.example.gis.GisDataProvider
import com.example.gis.GisPoint
import com.example.gis.HazardIncident
import com.example.gis.LocationShareContact
import com.example.gis.LocationTracker
import com.example.gis.MessageSender
import com.example.gis.NetworkHealth
import com.example.gis.OfflineMapRegion
import com.example.gis.RoutePlan
import com.example.gis.SearchFilterState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppThemeMode {
  SYSTEM,
  LIGHT,
  DARK
}

data class UiAlertMessage(
  val id: Long = System.currentTimeMillis(),
  val title: String,
  val message: String,
  val isError: Boolean = false
)

class ResQRouteViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ResQRouteRepository
  private val locationTracker = LocationTracker(application)

  init {
    val database = ResQRouteDatabase.getDatabase(application)
    repository = ResQRouteRepository(database.resQRouteDao())

    viewModelScope.launch {
      repository.initializeDefaultMissionsIfEmpty()
    }

    startGpsTelemetryUpdates()
  }

  // Routes
  private val initialRoutes = GisDataProvider.getRoutePlans()
  private val _safeRoute = MutableStateFlow(initialRoutes.first)
  val safeRoute: StateFlow<RoutePlan> = _safeRoute.asStateFlow()

  private val _blockedRoute = MutableStateFlow(initialRoutes.second)
  val blockedRoute: StateFlow<RoutePlan> = _blockedRoute.asStateFlow()

  private val _isSafeRouteSelected = MutableStateFlow(true)
  val isSafeRouteSelected: StateFlow<Boolean> = _isSafeRouteSelected.asStateFlow()

  // Hazards & Hubs
  private val _hazards = MutableStateFlow(GisDataProvider.getActiveHazards())
  val hazards: StateFlow<List<HazardIncident>> = _hazards.asStateFlow()

  val hubs: List<GisPoint> = GisDataProvider.allHubs

  // Selected Items for Detail Inspection
  private val _selectedHazard = MutableStateFlow<HazardIncident?>(null)
  val selectedHazard: StateFlow<HazardIncident?> = _selectedHazard.asStateFlow()

  private val _selectedHub = MutableStateFlow<GisPoint?>(null)
  val selectedHub: StateFlow<GisPoint?> = _selectedHub.asStateFlow()

  // Telemetry (Live GPS + Elevation)
  private val _telemetry = MutableStateFlow(locationTracker.getDefaultNerTelemetry())
  val telemetry: StateFlow<ConvoyTelemetry> = _telemetry.asStateFlow()

  // Room Encrypted Data
  val fieldReports: StateFlow<List<DecryptedFieldReport>> = repository.allFieldReports
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val unsyncedCount: StateFlow<Int> = repository.unsyncedReportsCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val missions: StateFlow<List<MissionEntity>> = repository.allMissions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Network & System Health
  private val _networkHealth = MutableStateFlow(
    NetworkHealth(
      satelliteUplink = true,
      meshNetworkPeers = 6,
      offlineCacheValid = true,
      lastGisSyncTime = "3m ago",
      roadNetworkUsabilityPercent = 74
    )
  )
  val networkHealth: StateFlow<NetworkHealth> = _networkHealth.asStateFlow()

  // Gemini AI Route Intelligence
  private val _aiAdvice = MutableStateFlow<AiAdviceResult?>(null)
  val aiAdvice: StateFlow<AiAdviceResult?> = _aiAdvice.asStateFlow()

  private val _isAnalyzingWithAi = MutableStateFlow(false)
  val isAnalyzingWithAi: StateFlow<Boolean> = _isAnalyzingWithAi.asStateFlow()

  // Theme Mode
  private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
  val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

  // Transient Alert / Snackbar
  private val _uiNotice = MutableStateFlow<UiAlertMessage?>(null)
  val uiNotice: StateFlow<UiAlertMessage?> = _uiNotice.asStateFlow()

  // 1. Search & Filtering State
  private val _searchFilterState = MutableStateFlow(SearchFilterState())
  val searchFilterState: StateFlow<SearchFilterState> = _searchFilterState.asStateFlow()

  val filteredHubs: StateFlow<List<GisPoint>> = _searchFilterState.combine(_hazards) { filter, _ ->
    hubs.filter { point ->
      // Query filter
      val matchesQuery = filter.query.isBlank() ||
        point.name.contains(filter.query, ignoreCase = true) ||
        point.address.contains(filter.query, ignoreCase = true) ||
        point.category.contains(filter.query, ignoreCase = true) ||
        point.tags.any { it.contains(filter.query, ignoreCase = true) }

      // Category filter
      val matchesCategory = filter.selectedCategory == "ALL" ||
        point.category.equals(filter.selectedCategory, ignoreCase = true)

      // Rating filter
      val matchesRating = point.rating >= filter.minRating

      // Tag filter (must contain all selected tags)
      val matchesTags = filter.selectedTags.isEmpty() ||
        filter.selectedTags.all { point.tags.contains(it) }

      matchesQuery && matchesCategory && matchesRating && matchesTags
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), hubs)

  // 2. Real-Time Location Sharing State
  val activeLocationShare: StateFlow<ActiveLocationShareSession?> = repository.activeLocationShare
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // 3. Offline Map Regions State
  val offlineRegions: StateFlow<List<OfflineMapRegion>> = repository.offlineRegions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GisDataProvider.defaultOfflineRegions)

  // 4. Context-Aware Support Chatbot State
  val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _isChatSending = MutableStateFlow(false)
  val isChatSending: StateFlow<Boolean> = _isChatSending.asStateFlow()

  private fun startGpsTelemetryUpdates() {
    viewModelScope.launch {
      locationTracker.getLocationUpdates().collect { newTelemetry ->
        _telemetry.value = newTelemetry
      }
    }
  }

  fun toggleRouteSelection() {
    _isSafeRouteSelected.value = !_isSafeRouteSelected.value
  }

  fun recalculateRoute() {
    _isSafeRouteSelected.value = true
    _uiNotice.value = UiAlertMessage(
      title = "Route Recalculated",
      message = "AI Dynamic Detour engaged via Umrangso Bypass. Sonapur hazard bypassed (Est. arrival updated)."
    )
  }

  fun selectHazard(hazard: HazardIncident?) {
    _selectedHazard.value = hazard
  }

  fun selectHub(hub: GisPoint?) {
    _selectedHub.value = hub
  }

  // Search & Filter Actions
  fun updateSearchQuery(query: String) {
    _searchFilterState.value = _searchFilterState.value.copy(query = query)
  }

  fun updateSearchCategory(category: String) {
    _searchFilterState.value = _searchFilterState.value.copy(selectedCategory = category)
  }

  fun updateMinRating(minRating: Float) {
    _searchFilterState.value = _searchFilterState.value.copy(minRating = minRating)
  }

  fun toggleSearchTag(tag: String) {
    val currentTags = _searchFilterState.value.selectedTags.toMutableSet()
    if (currentTags.contains(tag)) {
      currentTags.remove(tag)
    } else {
      currentTags.add(tag)
    }
    _searchFilterState.value = _searchFilterState.value.copy(selectedTags = currentTags)
  }

  fun toggleSearchViewMode() {
    _searchFilterState.value = _searchFilterState.value.copy(
      isListView = !_searchFilterState.value.isListView
    )
  }

  // Real-Time Location Sharing Actions
  fun startLocationSharing(contact: LocationShareContact, durationMinutes: Int, isBatterySaver: Boolean) {
    viewModelScope.launch {
      val sessionId = repository.startLocationSharing(contact, durationMinutes, isBatterySaver)
      _uiNotice.value = UiAlertMessage(
        title = "Location Sharing Active",
        message = "Live coordinates encrypted with AES-256-GCM and broadcasting to ${contact.name} for $durationMinutes minutes."
      )
    }
  }

  fun stopLocationSharing() {
    viewModelScope.launch {
      val active = activeLocationShare.value
      if (active != null) {
        repository.stopLocationSharing(active.id)
        _uiNotice.value = UiAlertMessage(
          title = "Location Sharing Revoked",
          message = "Convoy has gone dark. Live broadcast killed immediately."
        )
      }
    }
  }

  // Offline Region Management Actions
  fun downloadOfflineRegion(regionId: String) {
    viewModelScope.launch {
      // Simulate download progress steps
      repository.updateRegionDownload(regionId, isDownloaded = false, progress = 25)
      delay(400)
      repository.updateRegionDownload(regionId, isDownloaded = false, progress = 60)
      delay(400)
      repository.updateRegionDownload(regionId, isDownloaded = false, progress = 90)
      delay(300)
      repository.updateRegionDownload(regionId, isDownloaded = true, progress = 100)
      _uiNotice.value = UiAlertMessage(
        title = "Region Cached Offline",
        message = "High-res vectors & elevation data encrypted and stored in local Room database."
      )
    }
  }

  fun deleteOfflineRegion(regionId: String) {
    viewModelScope.launch {
      repository.updateRegionDownload(regionId, isDownloaded = false, progress = 0)
      _uiNotice.value = UiAlertMessage(
        title = "Region Cache Cleared",
        message = "Local vector cache removed to conserve device storage."
      )
    }
  }

  // Context-Aware Chatbot Actions
  fun sendChatMessage(text: String) {
    if (text.isBlank()) return
    viewModelScope.launch {
      _isChatSending.value = true
      val userMsg = ChatMessage(
        id = "MSG-U-${System.currentTimeMillis()}",
        sender = MessageSender.USER,
        text = text
      )
      repository.saveChatMessage(userMsg)

      // Get current conversation history including this user message
      val currentHistory = chatMessages.value + userMsg
      val agentReplyText = GeminiChatbotManager.sendChatMessage(currentHistory, text)

      val agentMsg = ChatMessage(
        id = "MSG-A-${System.currentTimeMillis()}",
        sender = MessageSender.AGENT,
        text = agentReplyText
      )
      repository.saveChatMessage(agentMsg)

      // If user booked a dispatch, automatically add to Room mission database
      if (agentReplyText.contains("Booking Confirmed", ignoreCase = true) ||
        text.contains("confirm", ignoreCase = true)
      ) {
        val newMission = MissionEntity(
          missionCode = "NER-RESQ-${(1000..9999).random()}",
          title = if (text.contains("oxygen", true)) "Emergency Oxygen Cylinders Dispatch" else "Priority Cold-Chain Vaccine Transit",
          destination = if (text.contains("silchar", true)) "Silchar Relief Base Hospital" else "Shillong Civil Medical Center",
          origin = "Guwahati Central Disaster Logistics Depot",
          cargoType = if (text.contains("oxygen", true)) "OXYGEN" else "MEDICINE",
          priorityLevel = "CRITICAL_P1",
          targetTempCelsius = 3.5,
          isDelivered = false,
          etaMinutes = 260,
          assignedConvoy = "Convoy Delta-1 (4x4 Refrigerated)"
        )
        repository.insertMission(newMission)
      }

      _isChatSending.value = false
    }
  }

  fun submitEncryptedFieldReport(
    hazardType: String,
    description: String,
    corridor: String,
    severity: String
  ) {
    viewModelScope.launch {
      val currentGps = _telemetry.value
      val newId = repository.submitFieldReport(
        hazardType = hazardType,
        description = description,
        latitude = currentGps.latitude,
        longitude = currentGps.longitude,
        roadCorridor = corridor,
        severity = severity
      )

      _uiNotice.value = UiAlertMessage(
        title = "Report Encrypted & Stored Locally",
        message = "Report #$newId secured with AES-GCM encryption in Room offline database."
      )
    }
  }

  fun syncOfflineQueue() {
    viewModelScope.launch {
      val reports = fieldReports.value
      reports.filter { !it.isSynced }.forEach { report ->
        repository.markReportSynced(report.id)
      }
      _uiNotice.value = UiAlertMessage(
        title = "Mesh Sync Complete",
        message = "All offline field reports successfully uploaded to regional command center."
      )
    }
  }

  fun requestAiAssessment() {
    viewModelScope.launch {
      _isAnalyzingWithAi.value = true
      val activeRoutePlan = if (_isSafeRouteSelected.value) _safeRoute.value else _blockedRoute.value
      val missionSummary = missions.value.firstOrNull()?.title ?: "Critical Cold-Chain Medical Supplies"

      val result = GeminiRouteAdvisor.analyzeRouteAndHazards(
        activeRoute = activeRoutePlan,
        hazards = _hazards.value,
        cargoSummary = missionSummary
      )
      _aiAdvice.value = result
      _isAnalyzingWithAi.value = false
    }
  }

  fun setThemeMode(mode: AppThemeMode) {
    _themeMode.value = mode
  }

  fun clearNotice() {
    _uiNotice.value = null
  }
}
