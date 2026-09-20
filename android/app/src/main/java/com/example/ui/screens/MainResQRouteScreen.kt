package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.viewmodel.ResQRouteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainResQRouteScreen(
  viewModel: ResQRouteViewModel,
  modifier: Modifier = Modifier
) {
  val pagerState = rememberPagerState(pageCount = { 4 })
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val snackbarHostState = remember { SnackbarHostState() }

  // State collectors
  val safeRoute by viewModel.safeRoute.collectAsState()
  val blockedRoute by viewModel.blockedRoute.collectAsState()
  val isSafeRouteSelected by viewModel.isSafeRouteSelected.collectAsState()
  val hazards by viewModel.hazards.collectAsState()
  val selectedHazard by viewModel.selectedHazard.collectAsState()
  val selectedHub by viewModel.selectedHub.collectAsState()
  val telemetry by viewModel.telemetry.collectAsState()
  val fieldReports by viewModel.fieldReports.collectAsState()
  val unsyncedCount by viewModel.unsyncedCount.collectAsState()
  val missions by viewModel.missions.collectAsState()
  val networkHealth by viewModel.networkHealth.collectAsState()
  val aiAdvice by viewModel.aiAdvice.collectAsState()
  val isAnalyzingWithAi by viewModel.isAnalyzingWithAi.collectAsState()
  val currentThemeMode by viewModel.themeMode.collectAsState()
  val uiNotice by viewModel.uiNotice.collectAsState()

  // New features state
  val searchState by viewModel.searchFilterState.collectAsState()
  val filteredPoints by viewModel.filteredHubs.collectAsState()
  val activeLocationShare by viewModel.activeLocationShare.collectAsState()
  val offlineRegions by viewModel.offlineRegions.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isChatSending by viewModel.isChatSending.collectAsState()

  // Display transient notices in Snackbar
  LaunchedEffect(uiNotice) {
    uiNotice?.let { notice ->
      snackbarHostState.showSnackbar("${notice.title}: ${notice.message}")
      viewModel.clearNotice()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize().testTag("main_scaffold"),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.Shield,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
              Text(
                text = "NER-ResQRoute",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Disaster Route Intelligence • NER Logistics",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
              )
            }
          }
        },
        actions = {
          // Offline status badge
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(RiskGreenSafe)
              )
              Spacer(Modifier.width(4.dp))
              Text(
                text = "Offline Mesh Ready",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        // Tab 0: GIS Map
        NavigationBarItem(
          selected = pagerState.currentPage == 0,
          onClick = {
            HapticFeedbackHelper.performTapHaptic(haptic)
            coroutineScope.launch { pagerState.animateScrollToPage(0) }
          },
          icon = { Icon(Icons.Default.Map, contentDescription = "GIS Terrain Map") },
          label = { Text("GIS Map", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_gis_map")
        )

        // Tab 1: Missions
        NavigationBarItem(
          selected = pagerState.currentPage == 1,
          onClick = {
            HapticFeedbackHelper.performTapHaptic(haptic)
            coroutineScope.launch { pagerState.animateScrollToPage(1) }
          },
          icon = {
            BadgedBox(badge = {
              Badge { Text("${missions.size}") }
            }) {
              Icon(Icons.Default.LocalShipping, contentDescription = "Mission Logistics")
            }
          },
          label = { Text("Missions", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_missions")
        )

        // Tab 2: Field Reports
        NavigationBarItem(
          selected = pagerState.currentPage == 2,
          onClick = {
            HapticFeedbackHelper.performTapHaptic(haptic)
            coroutineScope.launch { pagerState.animateScrollToPage(2) }
          },
          icon = {
            BadgedBox(badge = {
              if (unsyncedCount > 0) {
                Badge { Text("$unsyncedCount") }
              }
            }) {
              Icon(Icons.Default.Report, contentDescription = "Field Reports")
            }
          },
          label = { Text("Reports", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_field_reports")
        )

        // Tab 3: Resilience Command & AI
        NavigationBarItem(
          selected = pagerState.currentPage == 3,
          onClick = {
            HapticFeedbackHelper.performTapHaptic(haptic)
            coroutineScope.launch { pagerState.animateScrollToPage(3) }
          },
          icon = { Icon(Icons.Default.Dashboard, contentDescription = "Command & AI") },
          label = { Text("Command", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.testTag("nav_tab_command")
        )
      }
    }
  ) { paddingValues ->
    // Gesture-based navigation with smooth horizontal swiping across screen views
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .testTag("main_horizontal_pager")
    ) { page ->
      when (page) {
        0 -> GisMapTab(
          safeRoute = safeRoute,
          blockedRoute = blockedRoute,
          isSafeRouteSelected = isSafeRouteSelected,
          hazards = hazards,
          hubs = viewModel.hubs,
          telemetry = telemetry,
          selectedHazard = selectedHazard,
          selectedHub = selectedHub,
          searchState = searchState,
          filteredPoints = filteredPoints,
          activeLocationShare = activeLocationShare,
          offlineRegions = offlineRegions,
          chatMessages = chatMessages,
          isChatSending = isChatSending,
          onToggleRoute = { viewModel.toggleRouteSelection() },
          onRecalculateRoute = { viewModel.recalculateRoute() },
          onSelectHazard = { viewModel.selectHazard(it) },
          onSelectHub = { viewModel.selectHub(it) },
          onQueryChange = { viewModel.updateSearchQuery(it) },
          onCategoryChange = { viewModel.updateSearchCategory(it) },
          onMinRatingChange = { viewModel.updateMinRating(it) },
          onToggleTag = { viewModel.toggleSearchTag(it) },
          onToggleViewMode = { viewModel.toggleSearchViewMode() },
          onStartLocationSharing = { contact, duration, batterySaver ->
            viewModel.startLocationSharing(contact, duration, batterySaver)
          },
          onStopLocationSharing = { viewModel.stopLocationSharing() },
          onDownloadRegion = { viewModel.downloadOfflineRegion(it) },
          onDeleteRegion = { viewModel.deleteOfflineRegion(it) },
          onSendChatMessage = { viewModel.sendChatMessage(it) }
        )
        1 -> MissionsTab(
          missions = missions
        )
        2 -> FieldReportsTab(
          fieldReports = fieldReports,
          unsyncedCount = unsyncedCount,
          telemetry = telemetry,
          onSubmitReport = { type, desc, corridor, severity ->
            viewModel.submitEncryptedFieldReport(type, desc, corridor, severity)
          },
          onSyncReports = { viewModel.syncOfflineQueue() }
        )
        3 -> ResilienceCommandTab(
          networkHealth = networkHealth,
          aiAdvice = aiAdvice,
          isAnalyzingWithAi = isAnalyzingWithAi,
          currentThemeMode = currentThemeMode,
          onRequestAiAssessment = { viewModel.requestAiAssessment() },
          onRecalculateRoute = { viewModel.recalculateRoute() },
          onSetThemeMode = { viewModel.setThemeMode(it) }
        )
      }
    }
  }
}
