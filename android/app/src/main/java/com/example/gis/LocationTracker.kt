package com.example.gis

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationTracker(private val context: Context) {

  private val fusedLocationClient: FusedLocationProviderClient =
    LocationServices.getFusedLocationProviderClient(context)

  fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
      context,
      android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
      ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
      ) == PackageManager.PERMISSION_GRANTED
  }

  @SuppressLint("MissingPermission")
  fun getLocationUpdates(): Flow<ConvoyTelemetry> = callbackFlow {
    if (!hasLocationPermission()) {
      // Fallback default telemetry for NER Convoy simulation
      trySend(getDefaultNerTelemetry())
      awaitClose {}
      return@callbackFlow
    }

    val locationRequest = LocationRequest.Builder(
      Priority.PRIORITY_HIGH_ACCURACY,
      5000L
    ).apply {
      setMinUpdateIntervalMillis(2000L)
      setMinUpdateDistanceMeters(5.0f)
    }.build()

    val callback = object : LocationCallback() {
      override fun onLocationResult(result: LocationResult) {
        val lastLocation = result.lastLocation ?: return
        val telemetry = mapLocationToTelemetry(lastLocation, isLive = true)
        trySend(telemetry)
      }
    }

    fusedLocationClient.requestLocationUpdates(
      locationRequest,
      callback,
      android.os.Looper.getMainLooper()
    )

    awaitClose {
      fusedLocationClient.removeLocationUpdates(callback)
    }
  }

  fun getDefaultNerTelemetry(): ConvoyTelemetry {
    // Convoy positioned along the Umrangso bypass route
    val defaultLat = 25.5420
    val defaultLng = 92.4200
    val nearestHazard = calculateDistanceKm(
      defaultLat, defaultLng,
      GisDataProvider.SONAPUR_BOTTLENECK.latitude,
      GisDataProvider.SONAPUR_BOTTLENECK.longitude
    )

    return ConvoyTelemetry(
      latitude = defaultLat,
      longitude = defaultLng,
      altitudeMeters = 840.0,
      speedKmh = 48.5,
      headingDegrees = 118.0f,
      accuracyMeters = 4.2f,
      satellitesVisible = 14,
      isGpsActive = false, // Simulation fallback until GPS is activated
      currentCorridor = "SH-7 Highland Plateau (En route to Silchar)",
      isNearHazard = nearestHazard < 25.0,
      nearestHazardDistanceKm = nearestHazard
    )
  }

  fun mapLocationToTelemetry(location: Location, isLive: Boolean): ConvoyTelemetry {
    val nearestHazard = calculateDistanceKm(
      location.latitude,
      location.longitude,
      GisDataProvider.SONAPUR_BOTTLENECK.latitude,
      GisDataProvider.SONAPUR_BOTTLENECK.longitude
    )

    return ConvoyTelemetry(
      latitude = location.latitude,
      longitude = location.longitude,
      altitudeMeters = location.altitude,
      speedKmh = if (location.hasSpeed()) location.speed * 3.6 else 42.0,
      headingDegrees = if (location.hasBearing()) location.bearing else 115.0f,
      accuracyMeters = if (location.hasAccuracy()) location.accuracy else 5.0f,
      satellitesVisible = 16,
      isGpsActive = isLive,
      currentCorridor = "Live GPS: NER Corrdior",
      isNearHazard = nearestHazard < 25.0,
      nearestHazardDistanceKm = nearestHazard
    )
  }

  companion object {
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
      val r = 6371.0 // Radius of earth in km
      val dLat = Math.toRadians(lat2 - lat1)
      val dLon = Math.toRadians(lon2 - lon1)
      val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(dLon / 2) * sin(dLon / 2)
      val c = 2 * atan2(sqrt(a), sqrt(1 - a))
      return r * c
    }
  }
}
