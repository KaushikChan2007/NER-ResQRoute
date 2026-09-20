package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gis.ActiveLocationShareSession
import com.example.gis.GisDataProvider
import com.example.gis.LocationShareContact
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.theme.RiskRedBlocked
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSharingDialog(
  activeSession: ActiveLocationShareSession?,
  onStartSharing: (contact: LocationShareContact, durationMins: Int, isBatterySaver: Boolean) -> Unit,
  onStopSharing: (sessionId: String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val contacts = GisDataProvider.defaultShareContacts

  var selectedContact by remember { mutableStateOf(contacts.first()) }
  var selectedDurationMinutes by remember { mutableStateOf(60) }
  var isBatterySaverEnabled by remember { mutableStateOf(true) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(if (activeSession != null) RiskGreenSafe.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.ShareLocation,
              contentDescription = null,
              tint = if (activeSession != null) RiskGreenSafe else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
          Column {
            Text(
              text = "Real-Time Location Sharing",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (activeSession != null) "Active Encrypted Broadcast" else "Select Contact & Time Limit",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close dialog")
        }
      }

      // If active session exists: Show Live Status and Killswitch
      if (activeSession != null && activeSession.isActive) {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("active_sharing_status_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(RiskGreenSafe)
                )
                Text(
                  text = "BROADCASTING LIVE",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = RiskGreenSafe
                )
              }

              // Countdown timer
              var remainingSec by remember {
                mutableLongStateOf(maxOf(0L, (activeSession.expiryTimestamp - System.currentTimeMillis()) / 1000L))
              }
              LaunchedEffect(activeSession.expiryTimestamp) {
                while (remainingSec > 0) {
                  delay(1000L)
                  remainingSec = maxOf(0L, (activeSession.expiryTimestamp - System.currentTimeMillis()) / 1000L)
                }
              }

              val mins = remainingSec / 60
              val secs = remainingSec % 60
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                  Text(
                    text = String.format("%02d:%02d left", mins, secs),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }

            Text(
              text = "Sharing with: ${activeSession.contact.name}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "${activeSession.contact.role} (${activeSession.contact.department})",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Security & battery mode
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Text("AES-256-GCM Encrypted", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
              }
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                  if (activeSession.isBatterySaverEnabled) Icons.Default.BatterySaver else Icons.Default.BatteryChargingFull,
                  contentDescription = null,
                  modifier = Modifier.size(14.dp),
                  tint = MaterialTheme.colorScheme.outline
                )
                Text(
                  if (activeSession.isBatterySaverEnabled) "30s Mesh Beacon" else "5s Real-time",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }

            // Revoke / Go Dark button
            Button(
              onClick = { onStopSharing(activeSession.id) },
              colors = ButtonDefaults.buttonColors(containerColor = RiskRedBlocked),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("revoke_sharing_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("REVOKE SHARING / GO DARK", fontWeight = FontWeight.Bold)
            }
          }
        }
      } else {
        // Create new sharing session
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(text = "1. Choose Command Contact", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(contacts) { contact ->
              val isSelected = selectedContact.id == contact.id
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("contact_item_${contact.id}")
                  .clickable { selectedContact = contact },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = contact.avatarInitials,
                      color = Color.White,
                      style = MaterialTheme.typography.labelMedium,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = contact.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(text = "${contact.role} • ${contact.department}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }

                  if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                  }
                }
              }
            }
          }

          // Duration selection
          Text(text = "2. Set Sharing Time Limit", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf(15 to "15m", 60 to "1 Hour", 240 to "4 Hours", 480 to "8 Hours").forEach { (mins, label) ->
              val isSelected = selectedDurationMinutes == mins
              FilterChip(
                selected = isSelected,
                onClick = { selectedDurationMinutes = mins },
                label = { Text(label, fontSize = 12.sp) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("duration_chip_$mins")
              )
            }
          }

          // Battery Saver Mode Toggle
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.BatterySaver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                  Text("Battery-Saver Mesh Beacon", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                  Text("Sends telemetry pulses every 30s with dead reckoning to save 85% battery.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              Switch(
                checked = isBatterySaverEnabled,
                onCheckedChange = { isBatterySaverEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("battery_saver_toggle")
              )
            }
          }

          // Privacy & Encryption Notice
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
              Text(
                text = "End-to-End Encrypted: Raw coordinates are sealed via AES-256-GCM before transmission.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Start Button
          Button(
            onClick = {
              onStartSharing(selectedContact, selectedDurationMinutes, isBatterySaverEnabled)
              onDismiss()
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("start_location_share_button"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text("START ENCRYPTED LOCATION SHARING", fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(Modifier.height(10.dp))
    }
  }
}
