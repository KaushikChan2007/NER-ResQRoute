package com.example.data.local

import com.example.gis.ActiveLocationShareSession
import com.example.gis.ChatMessage
import com.example.gis.GisDataProvider
import com.example.gis.LocationShareContact
import com.example.gis.MessageSender
import com.example.gis.OfflineMapRegion
import com.example.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResQRouteRepository(private val dao: ResQRouteDao) {

  val allFieldReports: Flow<List<DecryptedFieldReport>> = dao.getAllFieldReports().map { list ->
    list.map { entity ->
      DecryptedFieldReport(
        id = entity.id,
        hazardType = CryptoManager.decrypt(entity.encryptedHazardType),
        description = CryptoManager.decrypt(entity.encryptedDescription),
        latitude = entity.latitude,
        longitude = entity.longitude,
        roadCorridor = entity.roadCorridor,
        severity = entity.severity,
        timestamp = entity.timestamp,
        isSynced = entity.isSynced
      )
    }
  }

  val unsyncedReportsCount: Flow<Int> = dao.getUnsyncedReports().map { it.size }

  val allMissions: Flow<List<MissionEntity>> = dao.getAllMissions()

  val offlineRegions: Flow<List<OfflineMapRegion>> = dao.getAllOfflineRegions().map { list ->
    if (list.isEmpty()) {
      GisDataProvider.defaultOfflineRegions
    } else {
      list.map { entity ->
        OfflineMapRegion(
          id = entity.regionId,
          title = entity.title,
          stateCoverage = entity.stateCoverage,
          sizeMb = entity.sizeMb,
          isDownloaded = entity.isDownloaded,
          downloadProgress = entity.downloadProgress,
          lastUpdated = "Encrypted Local Cache",
          featuresList = listOf("High-res Topo Vectors", "Offline Routing Index", "Hospital Geofences")
        )
      }
    }
  }

  val activeLocationShare: Flow<ActiveLocationShareSession?> = dao.getActiveLocationShare().map { entity ->
    if (entity == null || !entity.isActive || System.currentTimeMillis() > entity.expiryTimestamp) {
      null
    } else {
      val contact = GisDataProvider.defaultShareContacts.find { it.name == entity.contactName }
        ?: LocationShareContact("C-TEMP", entity.contactName, entity.contactRole, "Disaster Ops", "+91 99999 00000", "HQ")

      ActiveLocationShareSession(
        id = entity.sessionId,
        contact = contact,
        startedTimestamp = entity.startedTimestamp,
        expiryTimestamp = entity.expiryTimestamp,
        durationMinutes = entity.durationMinutes,
        isBatterySaverEnabled = entity.isBatterySaverEnabled,
        updateIntervalSeconds = if (entity.isBatterySaverEnabled) 30 else 5,
        isEncrypted = entity.isEncrypted,
        encryptedPayloadPreview = CryptoManager.encrypt("GPS_TELEMETRY_${entity.sessionId}"),
        isActive = true
      )
    }
  }

  val chatMessages: Flow<List<ChatMessage>> = dao.getAllChatMessages().map { list ->
    list.map { entity ->
      ChatMessage(
        id = entity.messageId,
        sender = if (entity.sender == "USER") MessageSender.USER else MessageSender.AGENT,
        text = entity.text,
        timestamp = entity.timestamp
      )
    }
  }

  suspend fun submitFieldReport(
    hazardType: String,
    description: String,
    latitude: Double,
    longitude: Double,
    roadCorridor: String,
    severity: String
  ): Long {
    val encryptedType = CryptoManager.encrypt(hazardType)
    val encryptedDesc = CryptoManager.encrypt(description)

    val entity = FieldReportEntity(
      encryptedHazardType = encryptedType,
      encryptedDescription = encryptedDesc,
      latitude = latitude,
      longitude = longitude,
      roadCorridor = roadCorridor,
      severity = severity,
      isSynced = false
    )
    return dao.insertFieldReport(entity)
  }

  suspend fun markReportSynced(reportId: Long) {
    dao.markReportSynced(reportId)
  }

  suspend fun initializeDefaultMissionsIfEmpty() {
    val initial = GisDataProvider.getInitialMissions()
    dao.insertMissions(initial)

    // Also initialize default offline regions in Room
    val defaultRegions = GisDataProvider.defaultOfflineRegions.map {
      OfflineRegionEntity(
        regionId = it.id,
        title = it.title,
        stateCoverage = it.stateCoverage,
        sizeMb = it.sizeMb,
        isDownloaded = it.isDownloaded,
        downloadProgress = it.downloadProgress,
        encryptedTileCacheMetadata = CryptoManager.encrypt("TILES_CACHE_${it.id}_VECTOR_DATA")
      )
    }
    dao.insertOfflineRegions(defaultRegions)
  }

  suspend fun updateRegionDownload(regionId: String, isDownloaded: Boolean, progress: Int) {
    dao.updateRegionDownloadStatus(regionId, isDownloaded, progress, System.currentTimeMillis())
  }

  suspend fun startLocationSharing(
    contact: LocationShareContact,
    durationMinutes: Int,
    isBatterySaver: Boolean
  ): String {
    dao.deactivateAllLocationShares()
    val sessionId = "LOC-SHARE-${System.currentTimeMillis()}"
    val started = System.currentTimeMillis()
    val expiry = started + (durationMinutes * 60 * 1000L)

    val entity = LocationShareEntity(
      sessionId = sessionId,
      contactName = contact.name,
      contactRole = contact.role,
      durationMinutes = durationMinutes,
      startedTimestamp = started,
      expiryTimestamp = expiry,
      isBatterySaverEnabled = isBatterySaver,
      isEncrypted = true,
      isActive = true
    )
    dao.insertLocationShare(entity)
    return sessionId
  }

  suspend fun stopLocationSharing(sessionId: String) {
    dao.deactivateLocationShare(sessionId)
  }

  suspend fun saveChatMessage(message: ChatMessage) {
    val entity = ChatMessageEntity(
      messageId = message.id,
      sender = message.sender.name,
      text = message.text,
      timestamp = message.timestamp,
      bookingStep = message.bookingStep?.name
    )
    dao.insertChatMessage(entity)
  }

  suspend fun insertMission(mission: MissionEntity): Long {
    return dao.insertMission(mission)
  }
}

data class DecryptedFieldReport(
  val id: Long,
  val hazardType: String,
  val description: String,
  val latitude: Double,
  val longitude: Double,
  val roadCorridor: String,
  val severity: String,
  val timestamp: Long,
  val isSynced: Boolean
)
