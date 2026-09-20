package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiAdviceResult
import com.example.ai.GeminiRouteAdvisor
import com.example.gis.NetworkHealth
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.viewmodel.AppThemeMode

@Composable
fun ResilienceCommandTab(
  networkHealth: NetworkHealth,
  aiAdvice: AiAdviceResult?,
  isAnalyzingWithAi: Boolean,
  currentThemeMode: AppThemeMode,
  onRequestAiAssessment: () -> Unit,
  onRecalculateRoute: () -> Unit,
  onSetThemeMode: (AppThemeMode) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val hasGeminiKey = GeminiRouteAdvisor.isApiKeyConfigured()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Resilience Pathway Interactive Header (from Hackathon Architecture)
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("resilience_pathway_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "RESILIENCE PATHWAY (NER-LOGISTICS)",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )

          // Scrollable pipeline steps
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            PathwayStep(title = "INGEST", subtitle = "Risk Feeds", isActive = true)
            PathwayStep(title = "ASSESS", subtitle = "AI Hazard", isActive = true)
            PathwayStep(title = "OPTIMIZE", subtitle = "Weather GIS", isActive = true)
            PathwayStep(title = "ALERT", subtitle = "Field SMS", isActive = true)
            PathwayStep(title = "MONITOR", subtitle = "Offline Convoy", isActive = true)
            PathwayStep(title = "LEARN", subtitle = "Local Sync", isActive = true)
          }

          // Feedback loop actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = {
                HapticFeedbackHelper.performActionHaptic(context)
                onRecalculateRoute()
              },
              modifier = Modifier.weight(1f).height(44.dp).testTag("pathway_recalculate_button"),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("RECALCULATE ROUTE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                HapticFeedbackHelper.performActionHaptic(context)
                onRequestAiAssessment()
              },
              modifier = Modifier.weight(1f).height(44.dp).testTag("pathway_ai_assess_button"),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(Modifier.width(4.dp))
              Text("AI RISK UPDATE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Gemini 3.5 Flash Disaster-Aware Route Intelligence
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("gemini_intelligence_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(Modifier.width(8.dp))
              Text(
                text = "Disaster Route Intelligence",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            if (hasGeminiKey) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = RiskGreenSafe.copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier.size(6.dp).clip(CircleShape).background(RiskGreenSafe)
                  )
                  Spacer(Modifier.width(4.dp))
                  Text(
                    text = "Gemini Key Linked",
                    style = MaterialTheme.typography.labelSmall,
                    color = RiskGreenSafe,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          Text(
            text = "Synthesizes GIS terrain slope gradients, monsoonal precipitation metrics, and cold-chain mission urgency to formulate field-safe navigation strategies.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // AI Advice Card or Loading
          if (isAnalyzingWithAi) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
              Spacer(Modifier.width(10.dp))
              Text(
                text = "Consulting Gemini Disaster Reasoning Model...",
                style = MaterialTheme.typography.bodySmall
              )
            }
          } else if (aiAdvice != null) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = aiAdvice.statusLabel,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = aiAdvice.adviceText,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          // Secret Management info note for user
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.Key,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
              )
              Spacer(Modifier.width(8.dp))
              Text(
                text = "Secure Configuration: Set your GEMINI_API_KEY in the AI Studio Secrets panel. Keys are injected via BuildConfig at compile time and hidden from public repositories.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // 3. Network Health & Telemetry Metrics
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("network_health_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Command Center GIS Health",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            HealthMetricPill(
              label = "Road Usability",
              value = "${networkHealth.roadNetworkUsabilityPercent}%",
              isGood = networkHealth.roadNetworkUsabilityPercent > 60
            )
            HealthMetricPill(
              label = "Mesh Convoy Peers",
              value = "${networkHealth.meshNetworkPeers} active",
              isGood = true
            )
            HealthMetricPill(
              label = "Satellite Uplink",
              value = if (networkHealth.satelliteUplink) "Connected" else "Offline",
              isGood = networkHealth.satelliteUplink
            )
          }
        }
      }
    }

    // 4. Accessibility & Dark Mode Theme Switcher
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("theme_accessibility_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Visual & Accessibility Standards",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            text = "Earthy palette optimized for field contrast and readability. Adheres to WCAG 2.1 AAA contrast standards (>7:1) with vestibular-safe gentle animations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Theme Selector Chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = currentThemeMode == AppThemeMode.SYSTEM,
              onClick = {
                HapticFeedbackHelper.performTapHaptic(context as? androidx.compose.ui.hapticfeedback.HapticFeedback ?: return@FilterChip)
                onSetThemeMode(AppThemeMode.SYSTEM)
              },
              label = { Text("System Default") },
              leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp)) },
              modifier = Modifier.testTag("theme_chip_system")
            )

            FilterChip(
              selected = currentThemeMode == AppThemeMode.LIGHT,
              onClick = { onSetThemeMode(AppThemeMode.LIGHT) },
              label = { Text("Earthy Light") },
              leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
              modifier = Modifier.testTag("theme_chip_light")
            )

            FilterChip(
              selected = currentThemeMode == AppThemeMode.DARK,
              onClick = { onSetThemeMode(AppThemeMode.DARK) },
              label = { Text("Espresso Dark") },
              leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
              modifier = Modifier.testTag("theme_chip_dark")
            )
          }

          // Standards verification list
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = RiskGreenSafe, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("WCAG 2.1 High-Contrast Color Palette", style = MaterialTheme.typography.labelSmall)
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = RiskGreenSafe, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Vestibular Safe Animations (Subtle & Non-distracting)", style = MaterialTheme.typography.labelSmall)
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = RiskGreenSafe, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("AES-GCM KeyStore Encrypted Local Storage", style = MaterialTheme.typography.labelSmall)
          }
        }
      }
    }
  }
}

@Composable
fun PathwayStep(title: String, subtitle: String, isActive: Boolean) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    modifier = Modifier.width(100.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontSize = 11.sp
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
        fontSize = 9.sp
      )
    }
  }
}

@Composable
fun HealthMetricPill(label: String, value: String, isGood: Boolean) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = Modifier.width(100.dp)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = if (isGood) RiskGreenSafe else MaterialTheme.colorScheme.error
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp
      )
    }
  }
}
