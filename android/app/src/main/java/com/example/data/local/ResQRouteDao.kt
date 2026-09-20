package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResQRouteDao {
  // Field Reports
  @Query("SELECT * FROM field_reports ORDER BY timestamp DESC")
  fun getAllFieldReports(): Flow<List<FieldReportEntity>>

  @Query("SELECT * FROM field_reports WHERE isSynced = 0")
  fun getUnsyncedReports(): Flow<List<FieldReportEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFieldReport(report: FieldReportEntity): Long

  @Query("UPDATE field_reports SET isSynced = 1 WHERE id = :reportId")
  suspend fun markReportSynced(reportId: Long)

  @Query("DELETE FROM field_reports WHERE id = :reportId")
  suspend fun deleteFieldReport(reportId: Long)

  // Missions
  @Query("SELECT * FROM mission_cargo ORDER BY CASE priorityLevel WHEN 'CRITICAL_P1' THEN 1 WHEN 'HIGH_P2' THEN 2 ELSE 3 END ASC")
  fun getAllMissions(): Flow<List<MissionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMission(mission: MissionEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMissions(missions: List<MissionEntity>)

  @Update
  suspend fun updateMission(mission: MissionEntity)

  // Offline Regions
  @Query("SELECT * FROM offline_map_regions")
  fun getAllOfflineRegions(): Flow<List<OfflineRegionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOfflineRegion(region: OfflineRegionEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOfflineRegions(regions: List<OfflineRegionEntity>)

  @Query("UPDATE offline_map_regions SET isDownloaded = :isDownloaded, downloadProgress = :progress, lastUpdated = :timestamp WHERE regionId = :regionId")
  suspend fun updateRegionDownloadStatus(regionId: String, isDownloaded: Boolean, progress: Int, timestamp: Long)

  // Location Sharing Sessions
  @Query("SELECT * FROM location_shares WHERE isActive = 1 ORDER BY startedTimestamp DESC LIMIT 1")
  fun getActiveLocationShare(): Flow<LocationShareEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLocationShare(share: LocationShareEntity)

  @Query("UPDATE location_shares SET isActive = 0 WHERE sessionId = :sessionId")
  suspend fun deactivateLocationShare(sessionId: String)

  @Query("UPDATE location_shares SET isActive = 0")
  suspend fun deactivateAllLocationShares()

  // Chat Messages
  @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
  fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChatMessage(message: ChatMessageEntity): Long

  @Query("DELETE FROM chat_messages")
  suspend fun clearChatHistory()
}
