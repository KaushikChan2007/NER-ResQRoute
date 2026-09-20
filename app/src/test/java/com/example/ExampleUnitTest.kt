package com.example

import com.example.gis.GisDataProvider
import com.example.gis.LocationTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testGisDataProviderRoutes() {
    val (safeRoute, blockedRoute) = GisDataProvider.getRoutePlans()
    assertTrue(safeRoute.averageAccessibilityScore > 85)
    assertTrue(blockedRoute.averageAccessibilityScore < 40)
    assertTrue(blockedRoute.isBlocked)
    assertTrue(safeRoute.isAiRecommended)
    assertNotNull(GisDataProvider.SONAPUR_BOTTLENECK)
  }

  @Test
  fun testHaversineDistanceCalculation() {
    // Guwahati to Shillong distance is roughly ~70-90 km direct
    val dist = LocationTracker.calculateDistanceKm(
      26.1445, 91.7362, // Guwahati
      25.5788, 91.8933  // Shillong
    )
    assertTrue("Expected ~65-90 km, got $dist", dist in 60.0..95.0)
  }

  @Test
  fun testActiveHazardsData() {
    val hazards = GisDataProvider.getActiveHazards()
    assertTrue(hazards.isNotEmpty())
    val sonapurHazard = hazards.find { it.id == "HZ-101" }
    assertNotNull(sonapurHazard)
    assertEquals("NH-6 Sonapur Tunnel Approach", sonapurHazard?.highwayName)
  }
}
