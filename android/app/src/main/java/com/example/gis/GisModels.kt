package com.example.gis

data class GisPoint(
  val id: String,
  val name: String,
  val latitude: Double,
  val longitude: Double,
  val elevationMeters: Int,
  val hubType: HubType = HubType.WAYPOINT,
  val address: String = "National Highway Corridor, NER",
  val category: String = "Waystation",
  val rating: Float = 4.5f,
  val tags: List<String> = emptyList(),
  val contactPhone: String = "+91 361 223 4400",
  val description: String = "Strategic disaster logistics waypoint."
)

enum class HubType {
  PRIMARY_DEPOT,
  FIELD_HOSPITAL,
  REFUGE_CAMP,
  FORWARD_POST,
  WAYPOINT
}

enum class HazardSeverity {
  CRITICAL_BLOCKED,  // 0% accessibility (Landslide, Bridge Washout)
  MODERATE_WARNING,  // 40-60% accessibility (Mud flow, 4x4 only, slow)
  ADVISORY_MONITORED // 70-90% accessibility (Heavy rain, slippery)
}

enum class HazardType {
  LANDSLIDE,
  FLASH_FLOOD,
  BRIDGE_SUBSIDENCE,
  ROCKFALL_ZONE,
  MUD_FLOW
}

data class HazardIncident(
  val id: String,
  val title: String,
  val hazardType: HazardType,
  val severity: HazardSeverity,
  val location: GisPoint,
  val highwayName: String,
  val description: String,
  val reportedTimeAgo: String,
  val activeEvacuationRadiusMeters: Double = 500.0
)

data class RoadSegment(
  val id: String,
  val name: String,
  val highwayCode: String,
  val startPoint: GisPoint,
  val endPoint: GisPoint,
  val accessibilityScore: Int, // 0 to 100
  val isPassable: Boolean,
  val riskFactor: String,
  val slopePercent: Float,
  val rainfallMmPerHour: Float
)

data class ConvoyTelemetry(
  val latitude: Double,
  val longitude: Double,
  val altitudeMeters: Double,
  val speedKmh: Double,
  val headingDegrees: Float,
  val accuracyMeters: Float,
  val satellitesVisible: Int,
  val isGpsActive: Boolean,
  val currentCorridor: String,
  val isNearHazard: Boolean,
  val nearestHazardDistanceKm: Double
)

data class RoutePlan(
  val id: String,
  val title: String,
  val isAiRecommended: Boolean,
  val isBlocked: Boolean,
  val distanceKm: Double,
  val durationMinutes: Int,
  val averageAccessibilityScore: Int,
  val riskSummary: String,
  val waypoints: List<GisPoint>,
  val elevationMin: Int,
  val elevationMax: Int
)

data class NetworkHealth(
  val satelliteUplink: Boolean,
  val meshNetworkPeers: Int,
  val offlineCacheValid: Boolean,
  val lastGisSyncTime: String,
  val roadNetworkUsabilityPercent: Int
)

// Models for Advanced Search & Filtering
data class SearchFilterState(
  val query: String = "",
  val selectedCategory: String = "ALL",
  val minRating: Float = 0.0f,
  val selectedTags: Set<String> = emptySet(),
  val isListView: Boolean = false
)

// Models for Real-time Location Sharing
data class LocationShareContact(
  val id: String,
  val name: String,
  val role: String,
  val department: String,
  val phone: String,
  val avatarInitials: String
)

data class ActiveLocationShareSession(
  val id: String,
  val contact: LocationShareContact,
  val startedTimestamp: Long,
  val expiryTimestamp: Long,
  val durationMinutes: Int,
  val isBatterySaverEnabled: Boolean,
  val updateIntervalSeconds: Int,
  val isEncrypted: Boolean,
  val encryptedPayloadPreview: String,
  val isActive: Boolean
)

// Models for Offline Map Regions
data class OfflineMapRegion(
  val id: String,
  val title: String,
  val stateCoverage: String,
  val sizeMb: Double,
  val isDownloaded: Boolean,
  val downloadProgress: Int, // 0-100
  val isDownloading: Boolean = false,
  val lastUpdated: String,
  val featuresList: List<String>
)

// Models for Conversational Chatbot
data class ChatMessage(
  val id: String,
  val sender: MessageSender,
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val bookingStep: BookingStep? = null,
  val bookingPayload: BookingPayload? = null
)

enum class MessageSender {
  USER,
  AGENT
}

enum class BookingStep {
  CARGO_SELECTION,
  DESTINATION_CONFIRMATION,
  CONVOY_ASSIGNMENT,
  BOOKING_FINALIZED
}

data class BookingPayload(
  val cargoType: String,
  val origin: String,
  val destination: String,
  val priority: String,
  val assignedConvoy: String,
  val etaMinutes: Int,
  val tempCelsius: Double?
)
