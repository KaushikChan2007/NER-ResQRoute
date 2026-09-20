package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gis.ActiveLocationShareSession
import com.example.gis.ChatMessage
import com.example.gis.ConvoyTelemetry
import com.example.gis.GisDataProvider
import com.example.gis.GisPoint
import com.example.gis.HazardIncident
import com.example.gis.HazardSeverity
import com.example.gis.LocationShareContact
import com.example.gis.LocationTracker
import com.example.gis.OfflineMapRegion
import com.example.gis.RoutePlan
import com.example.gis.SearchFilterState
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.components.InteractiveGisMap
import com.example.ui.components.LocationSharingDialog
import com.example.ui.components.MapSearchFilterBar
import com.example.ui.components.OfflineRegionsDialog
import com.example.ui.components.SupportChatbotSheet
import com.example.ui.theme.RiskAmberWarning
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.theme.RiskRedBlocked
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GisMapTab(
  safeRoute: RoutePlan,
  blockedRoute: RoutePlan,
  isSafeRouteSelected: Boolean,
  hazards: List<HazardIncident>,
  hubs: List<GisPoint>,
  telemetry: ConvoyTelemetry,
  selectedHazard: HazardIncident?,
  selectedHub: GisPoint?,
  searchState: SearchFilterState,
  filteredPoints: List<GisPoint>,
  activeLocationShare: ActiveLocationShareSession?,
  offlineRegions: List<OfflineMapRegion>,
  chatMessages: List<ChatMessage>,
  isChatSending: Boolean,
  onToggleRoute: () -> Unit,
  onRecalculateRoute: () -> Unit,
  onSelectHazard: (HazardIncident?) -> Unit,
  onSelectHub: (GisPoint?) -> Unit,
  onQueryChange: (String) -> Unit,
  onCategoryChange: (String) -> Unit,
  onMinRatingChange: (Float) -> Unit,
  onToggleTag: (String) -> Unit,
  onToggleViewMode: () -> Unit,
  onStartLocationSharing: (LocationShareContact, Int, Boolean) -> Unit,
  onStopLocationSharing: () -> Unit,
  onDownloadRegion: (String) -> Unit,
  onDeleteRegion: (String) -> Unit,
  onSendChatMessage: (String) -> Unit
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  // Sheet dialog states
  var showLocationShareDialog by remember { mutableStateOf(false) }
  var showOfflineRegionsDialog by remember { mutableStateOf(false) }
  var showSupportChatbot by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Interactive Canvas Map (Pinch to Zoom, Pan, Contours, Rivers, Roads, Hazards, Search highlights)
    InteractiveGisMap(
      modifier = Modifier.fillMaxSize(),
      activeRoute = if (isSafeRouteSelected) safeRoute else blockedRoute,
      alternateRoute = if (isSafeRouteSelected) blockedRoute else safeRoute,
      hazards = hazards,
      hubs = hubs,
      telemetry = telemetry,
      isSafeRouteSelected = isSafeRouteSelected,
      highlightedPointIds = filteredPoints.map { it.id }.toSet(),
      activeLocationSharing = activeLocationShare,
      onSelectHazard = { hazard ->
        HapticFeedbackHelper.performHazardAlertHaptic(context)
        onSelectHazard(hazard)
      },
      onSelectHub = { hub ->
        HapticFeedbackHelper.performTapHaptic(haptic)
        onSelectHub(hub)
      },
      onRecalculateRoute = onRecalculateRoute
    )

    // 2. Search & Filtering Bar (Top of Screen)
    Column(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      MapSearchFilterBar(
        searchState = searchState,
        onQueryChange = onQueryChange,
        onCategoryChange = onCategoryChange,
        onMinRatingChange = onMinRatingChange,
        onToggleTag = onToggleTag,
        onToggleViewMode = onToggleViewMode,
        filteredPoints = filteredPoints,
        telemetry = telemetry,
        onSelectPoint = { onSelectHub(it) }
      )

      // Proximity Hazard Warning Banner (if near hazard)
      if (telemetry.isNearHazard) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = RiskRedBlocked.copy(alpha = 0.95f),
          shadowElevation = 4.dp,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hazard_proximity_banner")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.ReportProblem,
              contentDescription = "Hazard Warning",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "ALERT: Sonapur Landslide Zone (~${telemetry.nearestHazardDistanceKm.toInt()} km)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Road 0% accessible. Recalculate route to engage Dima Hasao bypass.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // 3. Search Results List View Overlay (when user toggles List view mode)
    if (searchState.isListView) {
      Surface(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxSize()
          .padding(top = 130.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)
          .testTag("search_results_list_overlay"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 8.dp
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Search Results (${filteredPoints.size} found)",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onToggleViewMode) {
              Icon(Icons.Default.Close, contentDescription = "Close list view")
            }
          }

          if (filteredPoints.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text("No matching places or POIs found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          } else {
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(filteredPoints, key = { it.id }) { point ->
                val distKm = LocationTracker.calculateDistanceKm(
                  telemetry.latitude, telemetry.longitude,
                  point.latitude, point.longitude
                )
                Card(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                      onSelectHub(point)
                      onToggleViewMode() // Switch back to map to see it
                    }
                    .testTag("point_list_item_${point.id}"),
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                  Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(text = point.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                      ) {
                        Text(
                          text = "${point.rating} ★",
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.primary,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }
                    Text(
                      text = "${point.category} • ~${distKm.toInt()} km away • Elev: ${point.elevationMeters}m",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = point.address,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      point.tags.take(3).forEach { tag ->
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = MaterialTheme.colorScheme.surface
                        ) {
                          Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontSize = 10.sp
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // 4. Quick Actions Floating HUD (Right Column)
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Real-Time Location Sharing HUD Button
      FilledIconButton(
        onClick = {
          HapticFeedbackHelper.performTapHaptic(haptic)
          showLocationShareDialog = true
        },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = if (activeLocationShare != null) RiskGreenSafe else MaterialTheme.colorScheme.surfaceVariant,
          contentColor = if (activeLocationShare != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
          .size(46.dp)
          .testTag("open_location_share_button")
      ) {
        Icon(Icons.Default.ShareLocation, contentDescription = "Real-Time Location Sharing")
      }

      // Download Offline Map Regions HUD Button
      FilledIconButton(
        onClick = {
          HapticFeedbackHelper.performTapHaptic(haptic)
          showOfflineRegionsDialog = true
        },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier
          .size(46.dp)
          .testTag("open_offline_regions_button")
      ) {
        Icon(Icons.Default.CloudDownload, contentDescription = "Download Offline Map Regions")
      }

      // Tactical AI Dispatch Support Chatbot FAB
      FloatingActionButton(
        onClick = {
          HapticFeedbackHelper.performTapHaptic(haptic)
          showSupportChatbot = true
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
          .size(50.dp)
          .testTag("open_chatbot_button")
      ) {
        Icon(Icons.Default.HeadsetMic, contentDescription = "Tactical Dispatch Support Agent")
      }
    }

    // 5. Bottom Route Orchestration & Recalculate Card (when not in list view)
    if (!searchState.isListView) {
      Surface(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 10.dp)
          .testTag("route_orchestration_card"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isSafeRouteSelected) "Active: AI Weather-Resilient Bypass" else "Active: NH-6 Direct Corridor",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (isSafeRouteSelected) "Guwahati → Shillong → Umrangso → Silchar" else "Guwahati → Sonapur Pass (BLOCKED) → Silchar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSafeRouteSelected) RiskGreenSafe.copy(alpha = 0.15f) else RiskRedBlocked.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .background(
                      if (isSafeRouteSelected) RiskGreenSafe else RiskRedBlocked,
                      CircleShape
                    )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                  text = if (isSafeRouteSelected) "94% CLEAR" else "BLOCKED",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (isSafeRouteSelected) RiskGreenSafe else RiskRedBlocked
                )
              }
            }
          }

          // Toggle Route Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilledTonalButton(
              onClick = {
                HapticFeedbackHelper.performTapHaptic(haptic)
                onToggleRoute()
              },
              modifier = Modifier
                .weight(1f)
                .testTag("toggle_alternate_route_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text("Switch Route", fontSize = 12.sp)
            }

            Button(
              onClick = {
                HapticFeedbackHelper.performHazardAlertHaptic(context)
                onRecalculateRoute()
              },
              modifier = Modifier
                .weight(1f)
                .testTag("recalculate_ai_detour_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text("AI Detour", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 6. Hazard Inspection Sheet
    selectedHazard?.let { hazard ->
      ModalBottomSheet(
        onDismissRequest = { onSelectHazard(null) },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = RiskRedBlocked,
                modifier = Modifier.size(24.dp)
              )
              Spacer(Modifier.width(8.dp))
              Text(
                text = hazard.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            IconButton(onClick = { onSelectHazard(null) }) {
              Icon(Icons.Default.Close, contentDescription = "Close dialog")
            }
          }

          Text(
            text = "Corridor: ${hazard.highwayName} • Reported ${hazard.reportedTimeAgo}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )

          Text(
            text = hazard.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ) {
            Text(
              text = "Recommended Action: " + if (hazard.severity == HazardSeverity.CRITICAL_BLOCKED) {
                "Engage AI Weather-Resilient Detour immediately to bypass blockage"
              } else {
                "Proceed with caution under 25 km/h with 4x4 engaged"
              },
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(10.dp)
            )
          }

          Button(
            onClick = {
              onRecalculateRoute()
              onSelectHazard(null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("Engage Weather-Resilient Detour")
          }
          Spacer(Modifier.height(16.dp))
        }
      }
    }

    // 7. Dialog Overlays
    if (showLocationShareDialog) {
      LocationSharingDialog(
        activeSession = activeLocationShare,
        onStartSharing = { contact, duration, batterySaver ->
          onStartLocationSharing(contact, duration, batterySaver)
        },
        onStopSharing = {
          onStopLocationSharing()
        },
        onDismiss = { showLocationShareDialog = false }
      )
    }

    if (showOfflineRegionsDialog) {
      OfflineRegionsDialog(
        regions = offlineRegions,
        onDownloadRegion = onDownloadRegion,
        onDeleteRegion = onDeleteRegion,
        onDismiss = { showOfflineRegionsDialog = false }
      )
    }

    if (showSupportChatbot) {
      SupportChatbotSheet(
        messages = chatMessages,
        isSending = isChatSending,
        onSendMessage = onSendChatMessage,
        onDismiss = { showSupportChatbot = false }
      )
    }
  }
}
