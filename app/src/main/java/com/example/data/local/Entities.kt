package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "field_reports")
data class FieldReportEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val encryptedHazardType: String, // Encrypted hazard name (e.g. Landslide, Flash Flood)
  val encryptedDescription: String, // Encrypted on-ground notes
  val latitude: Double,
  val longitude: Double,
  val roadCorridor: String, // e.g. "NH-6 Sonapur Segment"
  val severity: String, // "CRITICAL_BLOCKED", "MODERATE_WARNING", "LOW_PASSABLE"
  val timestamp: Long = System.currentTimeMillis(),
  val isSynced: Boolean = false // Offline-first queue flag
)

@Entity(tableName = "mission_cargo")
data class MissionEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val missionCode: String, // e.g. "MED-NER-04"
  val title: String, // e.g. "Essential Cold-Chain Insulin & Vaccines"
  val destination: String, // e.g. "Shillong Civil Hospital"
  val origin: String, // e.g. "Guwahati Central Disaster Depot"
  val cargoType: String, // "MEDICINE", "OXYGEN", "FOOD_RATIONS", "BLOOD_PLASMA"
  val priorityLevel: String, // "CRITICAL_P1", "HIGH_P2", "STANDARD_P3"
  val targetTempCelsius: Double?, // e.g. 4.0 for cold-chain
  val isDelivered: Boolean = false,
  val etaMinutes: Int,
  val assignedConvoy: String = "Convoy Delta-1"
)

@Entity(tableName = "offline_map_regions")
data class OfflineRegionEntity(
  @PrimaryKey
  val regionId: String,
  val title: String,
  val stateCoverage: String,
  val sizeMb: Double,
  val isDownloaded: Boolean,
  val downloadProgress: Int,
  val encryptedTileCacheMetadata: String,
  val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "location_shares")
data class LocationShareEntity(
  @PrimaryKey
  val sessionId: String,
  val contactName: String,
  val contactRole: String,
  val durationMinutes: Int,
  val startedTimestamp: Long,
  val expiryTimestamp: Long,
  val isBatterySaverEnabled: Boolean,
  val isEncrypted: Boolean,
  val isActive: Boolean
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val messageId: String,
  val sender: String, // "USER" or "AGENT"
  val text: String,
  val timestamp: Long = System.currentTimeMillis(),
  val bookingStep: String? = null,
  val bookingPayloadJson: String? = null
)
