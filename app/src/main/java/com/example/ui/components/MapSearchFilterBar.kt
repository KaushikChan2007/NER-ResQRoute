package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gis.ConvoyTelemetry
import com.example.gis.GisPoint
import com.example.gis.LocationTracker
import com.example.gis.SearchFilterState
import com.example.ui.theme.RiskGreenSafe
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchFilterBar(
  searchState: SearchFilterState,
  onQueryChange: (String) -> Unit,
  onCategoryChange: (String) -> Unit,
  onMinRatingChange: (Float) -> Unit,
  onToggleTag: (String) -> Unit,
  onToggleViewMode: () -> Unit,
  filteredPoints: List<GisPoint>,
  telemetry: ConvoyTelemetry,
  onSelectPoint: (GisPoint) -> Unit,
  modifier: Modifier = Modifier
) {
  var isFilterExpanded by remember { mutableStateOf(false) }
  var isListSheetOpen by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val scope = rememberCoroutineScope()

  val categories = listOf("ALL", "Relief Depot", "Field Hospital", "Mountain Pass", "Hazard Zone", "Helipad & Evac")
  val availableTags = listOf("#ColdChainReady", "#OxygenSupply", "#Helipad", "#4x4Access", "#MeshRelay")

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Top Search Bar
    Surface(
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
      shadowElevation = 4.dp,
      modifier = Modifier.fillMaxWidth().testTag("map_search_bar_surface")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          Icons.Default.Search,
          contentDescription = "Search places and addresses",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 6.dp, end = 6.dp)
        )

        OutlinedTextField(
          value = searchState.query,
          onValueChange = onQueryChange,
          placeholder = { Text("Search addresses, depots, hospitals...", fontSize = 13.sp) },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("map_search_input_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
          )
        )

        if (searchState.query.isNotEmpty()) {
          IconButton(onClick = { onQueryChange("") }) {
            Icon(Icons.Default.Clear, contentDescription = "Clear search query", modifier = Modifier.size(18.dp))
          }
        }

        // Toggle filter panel expand
        IconButton(
          onClick = { isFilterExpanded = !isFilterExpanded },
          modifier = Modifier.testTag("toggle_filters_button")
        ) {
          Icon(
            Icons.Default.FilterList,
            contentDescription = "Toggle category and tag filters",
            tint = if (isFilterExpanded || searchState.selectedCategory != "ALL" || searchState.selectedTags.isNotEmpty() || searchState.minRating > 0f) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.onSurfaceVariant
            }
          )
        }

        // View as List toggle button
        IconButton(
          onClick = { isListSheetOpen = true },
          modifier = Modifier.testTag("open_results_list_button")
        ) {
          Icon(
            Icons.AutoMirrored.Filled.ViewList,
            contentDescription = "View matching places list",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }

    // Expandable Filter Chips Drawer
    AnimatedVisibility(visible = isFilterExpanded) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth().testTag("filter_chips_panel")
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Category Row
          Text(text = "Category Filter", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.forEach { cat ->
              val isSelected = searchState.selectedCategory == cat
              FilterChip(
                selected = isSelected,
                onClick = { onCategoryChange(cat) },
                label = { Text(cat, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("category_chip_$cat")
              )
            }
          }

          // Rating and Tags Row
          Text(text = "Operational Readiness & Tags", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            // Rating 4.5+ chip
            FilterChip(
              selected = searchState.minRating >= 4.5f,
              onClick = { onMinRatingChange(if (searchState.minRating >= 4.5f) 0.0f else 4.5f) },
              label = { Text("4.5+ ★", fontSize = 11.sp) },
              leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp)) },
              modifier = Modifier.testTag("rating_filter_chip")
            )

            // Custom Tags
            availableTags.forEach { tag ->
              val isSelected = searchState.selectedTags.contains(tag)
              FilterChip(
                selected = isSelected,
                onClick = { onToggleTag(tag) },
                label = { Text(tag, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.testTag("tag_chip_$tag")
              )
            }
          }

          // Active matches count
          Text(
            text = "${filteredPoints.size} locations match criteria",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }

  // Search Results List Modal Bottom Sheet
  if (isListSheetOpen) {
    ModalBottomSheet(
      onDismissRequest = { isListSheetOpen = false },
      sheetState = sheetState,
      containerColor = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.85f)
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Search & Filter Results",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${filteredPoints.size} places located along NER corridors",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(onClick = {
            scope.launch { sheetState.hide() }.invokeOnCompletion { isListSheetOpen = false }
          }) {
            Icon(Icons.Default.Clear, contentDescription = "Close list view")
          }
        }

        Spacer(Modifier.height(8.dp))

        if (filteredPoints.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No places match your search query or filter tags.")
          }
        } else {
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(filteredPoints, key = { it.id }) { point ->
              val distKm = LocationTracker.calculateDistanceKm(
                telemetry.latitude, telemetry.longitude,
                point.latitude, point.longitude
              )

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("search_result_item_${point.id}")
                  .clickable {
                    onSelectPoint(point)
                    scope.launch { sheetState.hide() }.invokeOnCompletion { isListSheetOpen = false }
                  },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = point.name,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.weight(1f)
                    )

                    // Rating badge
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(2.dp))
                        Text(
                          text = "${point.rating}",
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.primary
                        )
                      }
                    }
                  }

                  // Category and distance
                  Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = point.category,
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary,
                      fontWeight = FontWeight.SemiBold
                    )
                    Text(
                      text = "• ~${distKm.toInt()} km from convoy",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "• Alt: ${point.elevationMeters}m",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.outline
                    )
                  }

                  // Address
                  Text(
                    text = point.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  // Tags
                  if (point.tags.isNotEmpty()) {
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      point.tags.forEach { tag ->
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = MaterialTheme.colorScheme.surface
                        ) {
                          Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
  }
}
