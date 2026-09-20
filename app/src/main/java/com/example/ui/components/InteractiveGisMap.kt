package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gis.ActiveLocationShareSession
import com.example.gis.ConvoyTelemetry
import com.example.gis.GisPoint
import com.example.gis.HazardIncident
import com.example.gis.HazardSeverity
import com.example.gis.HubType
import com.example.gis.LocationTracker
import com.example.gis.RoutePlan
import com.example.ui.theme.RiskAmberWarning
import com.example.ui.theme.RiskGreenSafe
import com.example.ui.theme.RiskRedBlocked
import kotlin.math.hypot

@Composable
fun InteractiveGisMap(
  modifier: Modifier = Modifier,
  activeRoute: RoutePlan,
  alternateRoute: RoutePlan,
  hazards: List<HazardIncident>,
  hubs: List<GisPoint>,
  telemetry: ConvoyTelemetry,
  isSafeRouteSelected: Boolean,
  highlightedPointIds: Set<String> = emptySet(),
  activeLocationSharing: ActiveLocationShareSession? = null,
  onSelectHazard: (HazardIncident) -> Unit,
  onSelectHub: (GisPoint) -> Unit,
  onRecalculateRoute: () -> Unit
) {
  var scale by remember { mutableFloatStateOf(1.0f) }
  var offset by remember { mutableStateOf(Offset.Zero) }
  var showTerrainContours by remember { mutableStateOf(true) }
  var selectedHubDetails by remember { mutableStateOf<GisPoint?>(null) }

  // Gentle, vestibular-safe pulse animation for hazard radius
  val infiniteTransition = rememberInfiniteTransition(label = "hazardPulse")
  val pulseRadiusFactor by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800),
      repeatMode = RepeatMode.Reverse
    ),
    label = "hazardPulseRadius"
  )

  val textMeasurer = rememberTextMeasurer()
  val isDark = isSystemInDarkTheme()

  BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val canvasWidth = constraints.maxWidth.toFloat()
    val canvasHeight = constraints.maxHeight.toFloat()

    // Geographic bounding box for Assam-Meghalaya-Barak corridor
    val minLat = 24.5
    val maxLat = 26.6
    val minLng = 91.4
    val maxLng = 93.4

    fun geoToCanvas(lat: Double, lng: Double): Offset {
      val normX = ((lng - minLng) / (maxLng - minLng)).toFloat()
      val normY = (1.0f - ((lat - minLat) / (maxLat - minLat)).toFloat())

      val rawX = normX * canvasWidth
      val rawY = normY * canvasHeight

      // Apply zoom & pan transform around center
      val centerX = canvasWidth / 2f
      val centerY = canvasHeight / 2f

      val scaledX = (rawX - centerX) * scale + centerX + offset.x
      val scaledY = (rawY - centerY) * scale + centerY + offset.y

      return Offset(scaledX, scaledY)
    }

    fun canvasToGeo(touchOffset: Offset): Pair<Double, Double> {
      val centerX = canvasWidth / 2f
      val centerY = canvasHeight / 2f

      val unscaledX = (touchOffset.x - offset.x - centerX) / scale + centerX
      val unscaledY = (touchOffset.y - offset.y - centerY) / scale + centerY

      val normX = (unscaledX / canvasWidth).coerceIn(0f, 1f)
      val normY = (unscaledY / canvasHeight).coerceIn(0f, 1f)

      val lng = minLng + normX * (maxLng - minLng)
      val lat = maxLat - normY * (maxLat - minLat)

      return Pair(lat, lng)
    }

    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .testTag("interactive_gis_canvas")
        .pointerInput(Unit) {
          detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.65f, 3.5f)
            val maxPanX = canvasWidth * (scale - 0.5f)
            val maxPanY = canvasHeight * (scale - 0.5f)
            offset = Offset(
              (offset.x + pan.x).coerceIn(-maxPanX, maxPanX),
              (offset.y + pan.y).coerceIn(-maxPanY, maxPanY)
            )
          }
        }
        .pointerInput(hazards, hubs) {
          detectTapGestures { tapOffset ->
            // Check tap collision with hazards first
            var hitHazard: HazardIncident? = null
            for (hazard in hazards) {
              val hPos = geoToCanvas(hazard.location.latitude, hazard.location.longitude)
              if (hypot(tapOffset.x - hPos.x, tapOffset.y - hPos.y) < 36.dp.toPx()) {
                hitHazard = hazard
                break
              }
            }

            if (hitHazard != null) {
              onSelectHazard(hitHazard)
              return@detectTapGestures
            }

            // Check tap collision with hubs
            for (hub in hubs) {
              val hubPos = geoToCanvas(hub.latitude, hub.longitude)
              if (hypot(tapOffset.x - hubPos.x, tapOffset.y - hubPos.y) < 32.dp.toPx()) {
                selectedHubDetails = hub
                onSelectHub(hub)
                return@detectTapGestures
              }
            }
          }
        }
    ) {
      // 1. Draw Earthy Terrain Canvas Background
      drawTerrainCanvas(isDark, canvasWidth, canvasHeight)

      // 2. Draw GIS Mountain Contours & River Ribbons
      if (showTerrainContours) {
        drawGisElevationContours(isDark, ::geoToCanvas)
      }
      drawBrahmaputraAndBarakRivers(isDark, ::geoToCanvas)

      // 3. Draw Road Corridors & Accessibility Scores
      drawRouteNetwork(
        activeRoute = activeRoute,
        alternateRoute = alternateRoute,
        isSafeRouteSelected = isSafeRouteSelected,
        geoToCanvas = ::geoToCanvas,
        isDark = isDark,
        textMeasurer = textMeasurer
      )

      // 4. Draw Active Hazards (Landslide, Flash Floods)
      hazards.forEach { hazard ->
        drawHazardMarker(
          hazard = hazard,
          pos = geoToCanvas(hazard.location.latitude, hazard.location.longitude),
          pulseFactor = pulseRadiusFactor,
          textMeasurer = textMeasurer,
          isDark = isDark
        )
      }

      // 5. Draw Strategic Depots & Hubs
      hubs.forEach { hub ->
        val isHighlighted = highlightedPointIds.contains(hub.id)
        drawHubMarker(
          hub = hub,
          pos = geoToCanvas(hub.latitude, hub.longitude),
          isHighlighted = isHighlighted,
          pulseFactor = pulseRadiusFactor,
          textMeasurer = textMeasurer,
          isDark = isDark
        )
      }

      // 6. Draw Convoy Vehicle with Live GPS Marker
      val convoyPos = geoToCanvas(telemetry.latitude, telemetry.longitude)
      drawConvoyGpsMarker(
        pos = convoyPos,
        telemetry = telemetry,
        pulseFactor = pulseRadiusFactor,
        isDark = isDark,
        textMeasurer = textMeasurer
      )
    }

    // Floating Map HUD Controls (Top Left & Top Right)
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Zoom In
      FilledIconButton(
        onClick = { scale = (scale * 1.25f).coerceAtMost(3.5f) },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.size(44.dp).testTag("zoom_in_button")
      ) {
        Icon(Icons.Default.Add, contentDescription = "Zoom in map")
      }

      // Zoom Out
      FilledIconButton(
        onClick = { scale = (scale / 1.25f).coerceAtLeast(0.65f) },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.size(44.dp).testTag("zoom_out_button")
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Zoom out map")
      }

      // Center on Convoy
      FilledIconButton(
        onClick = {
          scale = 1.2f
          offset = Offset.Zero
        },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.size(44.dp).testTag("recenter_convoy_button")
      ) {
        Icon(Icons.Default.MyLocation, contentDescription = "Recenter on convoy GPS")
      }

      // Toggle Contours Layer
      FilledIconButton(
        onClick = { showTerrainContours = !showTerrainContours },
        colors = IconButtonDefaults.filledIconButtonColors(
          containerColor = if (showTerrainContours) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
          contentColor = if (showTerrainContours) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.size(44.dp).testTag("toggle_contours_button")
      ) {
        Icon(Icons.Default.Layers, contentDescription = "Toggle GIS terrain elevation contours")
      }
    }

    // Scale and GIS Legend Bar (Bottom Left)
    Surface(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(16.dp),
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
      shadowElevation = 2.dp
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Safe Route Indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp, 3.dp)
              .background(RiskGreenSafe, RoundedCornerShape(2.dp))
          )
          Spacer(Modifier.width(4.dp))
          Text("Passable (94%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        }

        // Blocked Route Indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp, 3.dp)
              .background(RiskRedBlocked, RoundedCornerShape(2.dp))
          )
          Spacer(Modifier.width(4.dp))
          Text("Blocked (24%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
        }

        // Scale
        Text(
          text = "GIS Scale ~50 km",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }

    // Active Location Sharing Banner (Top Center)
    if (activeLocationSharing != null) {
      Surface(
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
        shadowElevation = 4.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(RiskGreenSafe, CircleShape)
          )
          Text(
            text = "Live Location Sharing: ${activeLocationSharing.contact.name}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
        }
      }
    }

    // Selected Hub / POI Details Card (Bottom Center)
    selectedHubDetails?.let { hub ->
      val distKm = LocationTracker.calculateDistanceKm(
        telemetry.latitude, telemetry.longitude,
        hub.latitude, hub.longitude
      )
      Card(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth(0.92f)
          .padding(bottom = 72.dp)
          .testTag("selected_hub_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = hub.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "${hub.category} • ~${distKm.toInt()} km away • Alt: ${hub.elevationMeters}m",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "${hub.rating} ★",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = hub.address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = hub.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            androidx.compose.material3.TextButton(
              onClick = { selectedHubDetails = null }
            ) {
              Text("Dismiss", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

// Canvas Drawing Helper Functions

private fun DrawScope.drawTerrainCanvas(isDark: Boolean, width: Float, height: Float) {
  val baseTop = if (isDark) Color(0xFF1E1815) else Color(0xFFF7F3EE)
  val baseBottom = if (isDark) Color(0xFF130F0D) else Color(0xFFEBE3D7)

  drawRect(
    brush = Brush.verticalGradient(
      colors = listOf(baseTop, baseBottom),
      startY = 0f,
      endY = height
    )
  )
}

private fun DrawScope.drawGisElevationContours(isDark: Boolean, geoToCanvas: (Double, Double) -> Offset) {
  val contourColor = if (isDark) Color(0x1EF5ECE4) else Color(0x1F523B2A)
  val ridgeColor = if (isDark) Color(0x35E2B887) else Color(0x337A5338)

  // Khasi-Jaintia Hills elevation ridge lines
  val contour1 = Path().apply {
    val p1 = geoToCanvas(25.75, 91.50)
    val p2 = geoToCanvas(25.65, 91.90)
    val p3 = geoToCanvas(25.40, 92.30)
    val p4 = geoToCanvas(25.10, 92.70)
    moveTo(p1.x, p1.y)
    cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
  }
  drawPath(contour1, ridgeColor, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

  val contour2 = Path().apply {
    val p1 = geoToCanvas(25.90, 91.60)
    val p2 = geoToCanvas(25.50, 92.10)
    val p3 = geoToCanvas(25.25, 92.50)
    val p4 = geoToCanvas(24.95, 92.90)
    moveTo(p1.x, p1.y)
    cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
  }
  drawPath(contour2, contourColor, style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f))))

  // Barail Range / Dima Hasao ridge
  val contour3 = Path().apply {
    val p1 = geoToCanvas(25.60, 92.60)
    val p2 = geoToCanvas(25.35, 92.85)
    val p3 = geoToCanvas(25.15, 93.10)
    moveTo(p1.x, p1.y)
    quadraticTo(p2.x, p2.y, p3.x, p3.y)
  }
  drawPath(contour3, ridgeColor, style = Stroke(width = 2.0f, cap = StrokeCap.Round))
}

private fun DrawScope.drawBrahmaputraAndBarakRivers(isDark: Boolean, geoToCanvas: (Double, Double) -> Offset) {
  val riverColor = if (isDark) Color(0x403A7CA5) else Color(0x4D2A6F97)

  // Brahmaputra river curving along northern plains
  val bRiver = Path().apply {
    val p1 = geoToCanvas(26.18, 91.50)
    val p2 = geoToCanvas(26.15, 91.75)
    val p3 = geoToCanvas(26.25, 92.40)
    val p4 = geoToCanvas(26.55, 93.10)
    moveTo(p1.x, p1.y)
    cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
  }
  drawPath(bRiver, riverColor, style = Stroke(width = 7.0f, cap = StrokeCap.Round, join = StrokeJoin.Round))

  // Barak river in southern valley
  val barakRiver = Path().apply {
    val p1 = geoToCanvas(24.78, 92.60)
    val p2 = geoToCanvas(24.84, 92.80)
    val p3 = geoToCanvas(24.90, 93.10)
    moveTo(p1.x, p1.y)
    quadraticTo(p2.x, p2.y, p3.x, p3.y)
  }
  drawPath(barakRiver, riverColor, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
}

private fun DrawScope.drawRouteNetwork(
  activeRoute: RoutePlan,
  alternateRoute: RoutePlan,
  isSafeRouteSelected: Boolean,
  geoToCanvas: (Double, Double) -> Offset,
  isDark: Boolean,
  textMeasurer: TextMeasurer
) {
  // 1. Draw Blocked Legacy Route (Sonapur NH-6)
  val blockedPoints = listOf(
    geoToCanvas(26.1445, 91.7362), // Guwahati
    geoToCanvas(25.5788, 91.8933), // Shillong
    geoToCanvas(25.4526, 92.2036), // Jowai
    geoToCanvas(25.0440, 92.3680), // Sonapur Bottleneck (Landslide)
    geoToCanvas(24.8333, 92.7789)  // Silchar
  )

  val blockedPath = Path().apply {
    moveTo(blockedPoints[0].x, blockedPoints[0].y)
    for (i in 1 until blockedPoints.size) {
      lineTo(blockedPoints[i].x, blockedPoints[i].y)
    }
  }

  // Draw Blocked casing and dashed red line
  drawPath(
    path = blockedPath,
    color = RiskRedBlocked.copy(alpha = 0.85f),
    style = Stroke(
      width = 5.5f,
      pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f)),
      cap = StrokeCap.Round,
      join = StrokeJoin.Round
    )
  )

  // 2. Draw Safe AI Recommended Bypass (Via Umrangso Highland)
  val safePoints = listOf(
    geoToCanvas(26.1445, 91.7362), // Guwahati
    geoToCanvas(25.5788, 91.8933), // Shillong
    geoToCanvas(25.5100, 92.7400), // Umrangso Dima Hasao
    geoToCanvas(25.1700, 93.0200), // Haflong Pass
    geoToCanvas(24.8333, 92.7789)  // Silchar
  )

  val safePath = Path().apply {
    moveTo(safePoints[0].x, safePoints[0].y)
    for (i in 1 until safePoints.size) {
      lineTo(safePoints[i].x, safePoints[i].y)
    }
  }

  // Safe Route Glow & Solid Line
  drawPath(
    path = safePath,
    color = RiskGreenSafe.copy(alpha = 0.35f),
    style = Stroke(width = 12.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
  )
  drawPath(
    path = safePath,
    color = RiskGreenSafe,
    style = Stroke(width = 6.0f, cap = StrokeCap.Round, join = StrokeJoin.Round)
  )

  // Label along the safe route
  val midPoint = safePoints[2]
  drawText(
    textMeasurer = textMeasurer,
    text = "AI Bypass (94% Clear)",
    topLeft = Offset(midPoint.x + 8f, midPoint.y - 24f),
    style = TextStyle(
      color = if (isDark) Color(0xFFC7F3D0) else Color(0xFF1B4D25),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  )
}

private fun DrawScope.drawHazardMarker(
  hazard: HazardIncident,
  pos: Offset,
  pulseFactor: Float,
  textMeasurer: TextMeasurer,
  isDark: Boolean
) {
  val baseColor = when (hazard.severity) {
    HazardSeverity.CRITICAL_BLOCKED -> RiskRedBlocked
    HazardSeverity.MODERATE_WARNING -> RiskAmberWarning
    HazardSeverity.ADVISORY_MONITORED -> Color(0xFFD49A00)
  }

  // Warning pulse ripple
  drawCircle(
    color = baseColor.copy(alpha = 0.25f / pulseFactor),
    radius = 24.dp.toPx() * pulseFactor,
    center = pos
  )

  // Outer warning disc
  drawCircle(
    color = baseColor,
    radius = 12.dp.toPx(),
    center = pos
  )

  // Inner core
  drawCircle(
    color = Color.White,
    radius = 5.dp.toPx(),
    center = pos
  )

  // Hazard Title label
  drawText(
    textMeasurer = textMeasurer,
    text = "${hazard.title} (${hazard.reportedTimeAgo})",
    topLeft = Offset(pos.x + 18.dp.toPx(), pos.y - 12.dp.toPx()),
    style = TextStyle(
      color = if (isDark) Color(0xFFFFB4AB) else RiskRedBlocked,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  )
}

private fun DrawScope.drawHubMarker(
  hub: GisPoint,
  pos: Offset,
  isHighlighted: Boolean,
  pulseFactor: Float,
  textMeasurer: TextMeasurer,
  isDark: Boolean
) {
  val hubColor = when (hub.hubType) {
    HubType.PRIMARY_DEPOT -> Color(0xFF4A3222)
    HubType.FIELD_HOSPITAL -> Color(0xFF1B5E20)
    HubType.FORWARD_POST -> Color(0xFF7A4A28)
    HubType.REFUGE_CAMP -> Color(0xFF006699)
    HubType.WAYPOINT -> Color(0xFF8A7969)
  }

  // Draw Search Highlight Halo if matched
  if (isHighlighted) {
    drawCircle(
      color = Color(0xFFD97706).copy(alpha = 0.35f / pulseFactor),
      radius = 26.dp.toPx() * pulseFactor,
      center = pos
    )
    drawCircle(
      color = Color(0xFFD97706),
      radius = 16.dp.toPx(),
      center = pos,
      style = Stroke(width = 3.dp.toPx())
    )
  }

  // Hub Icon Base
  drawCircle(
    color = if (isDark) Color(0xFF332924) else Color(0xFFFFFFFF),
    radius = 11.dp.toPx(),
    center = pos
  )
  drawCircle(
    color = hubColor,
    radius = 8.dp.toPx(),
    center = pos
  )
  drawCircle(
    color = Color.White,
    radius = 3.dp.toPx(),
    center = pos
  )

  // Hub Label
  drawText(
    textMeasurer = textMeasurer,
    text = if (isHighlighted) "★ ${hub.name}" else hub.name,
    topLeft = Offset(pos.x - 30.dp.toPx(), pos.y + 14.dp.toPx()),
    style = TextStyle(
      color = if (isHighlighted) Color(0xFFD97706) else if (isDark) Color(0xFFEFE8E1) else Color(0xFF261C16),
      fontSize = 10.sp,
      fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold
    )
  )
}

private fun DrawScope.drawConvoyGpsMarker(
  pos: Offset,
  telemetry: ConvoyTelemetry,
  pulseFactor: Float,
  isDark: Boolean,
  textMeasurer: TextMeasurer
) {
  val gpsColor = Color(0xFF0284C7)

  // Live GPS Radar pulse
  drawCircle(
    color = gpsColor.copy(alpha = 0.3f / pulseFactor),
    radius = 28.dp.toPx() * pulseFactor,
    center = pos
  )

  // Heading indicator beam
  drawCircle(
    color = gpsColor,
    radius = 12.dp.toPx(),
    center = pos
  )

  // Inner center
  drawCircle(
    color = Color.White,
    radius = 5.dp.toPx(),
    center = pos
  )

  // Convoy Callout
  val speedText = "Convoy Delta • ${telemetry.speedKmh.toInt()} km/h • Alt ${telemetry.altitudeMeters.toInt()}m"
  drawText(
    textMeasurer = textMeasurer,
    text = speedText,
    topLeft = Offset(pos.x + 18.dp.toPx(), pos.y + 8.dp.toPx()),
    style = TextStyle(
      color = if (isDark) Color(0xFF90CAF9) else Color(0xFF0369A1),
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )
  )
}
