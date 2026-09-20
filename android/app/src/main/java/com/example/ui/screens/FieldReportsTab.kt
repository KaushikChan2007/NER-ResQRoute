package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DecryptedFieldReport
import com.example.gis.ConvoyTelemetry
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.theme.RiskAmberWarning
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.theme.RiskRedBlocked
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldReportsTab(
  fieldReports: List<DecryptedFieldReport>,
  unsyncedCount: Int,
  telemetry: ConvoyTelemetry,
  onSubmitReport: (hazardType: String, description: String, corridor: String, severity: String) -> Unit,
  onSyncReports: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val hazardOptions = listOf(
    "Landslide / Mudflow",
    "Flash Flood Overwash",
    "Bridge Structural Damage",
    "Rockfall Zone",
    "Road Fissure / Subsidence"
  )
  var selectedHazard by remember { mutableStateOf(hazardOptions[0]) }
  var isHazardExpanded by remember { mutableStateOf(false) }

  val corridorOptions = listOf(
    "NH-6 Sonapur Tunnel Corridor",
    "SH-7 Highland Plateau",
    "NH-27 Spur Umrangso Link",
    "NH-54 Haflong-Silchar",
    "NH-106 Guwahati-Shillong"
  )
  var selectedCorridor by remember { mutableStateOf(corridorOptions[0]) }
  var isCorridorExpanded by remember { mutableStateOf(false) }

  val severityOptions = listOf("CRITICAL_BLOCKED", "MODERATE_WARNING", "LOW_PASSABLE")
  var selectedSeverity by remember { mutableStateOf(severityOptions[0]) }

  var notes by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      // Header with Encryption Badge
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth().testTag("field_reporting_banner")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Report,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(Modifier.width(8.dp))
              Text(
                text = "On-Ground Field Reporting",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            // AES Encryption Badge
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surface
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.Lock,
                  contentDescription = "AES-GCM Encryption Active",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                  text = "AES-GCM Encrypted",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(Modifier.height(6.dp))
          Text(
            text = "Field feedback directly recalculates regional accessibility scores. All reports are encrypted locally before persistent Room storage.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
          )
        }
      }
    }

    // New Report Input Form
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("new_report_form_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Submit Hazard Observation",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          // Auto-captured GPS coordinate indicator
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              Icons.Default.MyLocation,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(
              text = "Auto-tagged GPS: ${"%.4f".format(telemetry.latitude)}° N, ${"%.4f".format(telemetry.longitude)}° E (Alt: ${telemetry.altitudeMeters.toInt()}m)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Hazard Type Dropdown
          ExposedDropdownMenuBox(
            expanded = isHazardExpanded,
            onExpandedChange = { isHazardExpanded = !isHazardExpanded }
          ) {
            OutlinedTextField(
              value = selectedHazard,
              onValueChange = {},
              readOnly = true,
              label = { Text("Hazard Type") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHazardExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth().testTag("hazard_type_dropdown")
            )
            ExposedDropdownMenu(
              expanded = isHazardExpanded,
              onDismissRequest = { isHazardExpanded = false }
            ) {
              hazardOptions.forEach { option ->
                DropdownMenuItem(
                  text = { Text(option) },
                  onClick = {
                    selectedHazard = option
                    isHazardExpanded = false
                  }
                )
              }
            }
          }

          // Corridor Dropdown
          ExposedDropdownMenuBox(
            expanded = isCorridorExpanded,
            onExpandedChange = { isCorridorExpanded = !isCorridorExpanded }
          ) {
            OutlinedTextField(
              value = selectedCorridor,
              onValueChange = {},
              readOnly = true,
              label = { Text("Road Corridor / Highway") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCorridorExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth().testTag("corridor_dropdown")
            )
            ExposedDropdownMenu(
              expanded = isCorridorExpanded,
              onDismissRequest = { isCorridorExpanded = false }
            ) {
              corridorOptions.forEach { corridor ->
                DropdownMenuItem(
                  text = { Text(corridor) },
                  onClick = {
                    selectedCorridor = corridor
                    isCorridorExpanded = false
                  }
                )
              }
            }
          }

          // Severity Filter Chips
          Text(text = "Obstruction Severity", style = MaterialTheme.typography.labelMedium)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            severityOptions.forEach { severity ->
              val isSelected = selectedSeverity == severity
              FilterChip(
                selected = isSelected,
                onClick = { selectedSeverity = severity },
                label = {
                  Text(
                    text = when (severity) {
                      "CRITICAL_BLOCKED" -> "Blocked"
                      "MODERATE_WARNING" -> "Slow (4x4)"
                      else -> "Passable"
                    },
                    fontSize = 12.sp
                  )
                },
                modifier = Modifier.testTag("severity_chip_$severity")
              )
            }
          }

          // Notes input
          OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Tactical Description / Observations") },
            placeholder = { Text("e.g. 50m mudflow across both lanes, heavy debris...") },
            modifier = Modifier.fillMaxWidth().testTag("report_notes_input"),
            minLines = 2,
            maxLines = 4
          )

          // Submit Button
          Button(
            onClick = {
              HapticFeedbackHelper.performActionHaptic(context)
              val desc = if (notes.isBlank()) "Ground observation logged via mobile convoy." else notes
              onSubmitReport(selectedHazard, desc, selectedCorridor, selectedSeverity)
              notes = ""
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_field_report_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("ENCRYPT & LOG TO LOCAL ROOM DB", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Offline Sync Status & History Section
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Encrypted Local Log (${fieldReports.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          if (unsyncedCount > 0) {
            Text(
              text = "$unsyncedCount pending mesh sync",
              style = MaterialTheme.typography.labelSmall,
              color = RiskAmberWarning
            )
          }
        }

        Button(
          onClick = {
            HapticFeedbackHelper.performActionHaptic(context)
            onSyncReports()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("sync_queue_button")
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(4.dp))
          Text("Mesh Sync", fontSize = 12.sp)
        }
      }
    }

    if (fieldReports.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "No field hazard reports logged yet. Use the form above to log observations.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    items(fieldReports, key = { it.id }) { report ->
      FieldReportItemCard(report = report)
    }
  }
}

@Composable
fun FieldReportItemCard(report: DecryptedFieldReport) {
  Card(
    modifier = Modifier.fillMaxWidth().testTag("field_report_card_${report.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = report.hazardType,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        val syncColor = if (report.isSynced) RiskGreenSafe else RiskAmberWarning
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = syncColor.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              if (report.isSynced) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
              contentDescription = null,
              tint = syncColor,
              modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
              text = if (report.isSynced) "Synced" else "Offline Queue",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = syncColor,
              fontSize = 10.sp
            )
          }
        }
      }

      Text(
        text = "Corridor: ${report.roadCorridor}",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
      )

      Text(
        text = report.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
      val formattedDate = dateFormat.format(Date(report.timestamp))
      Text(
        text = "Logged: $formattedDate • Decrypted from hardware KeyStore",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline,
        fontSize = 10.sp
      )
    }
  }
}
