package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    FieldReportEntity::class,
    MissionEntity::class,
    OfflineRegionEntity::class,
    LocationShareEntity::class,
    ChatMessageEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class ResQRouteDatabase : RoomDatabase() {
  abstract fun resQRouteDao(): ResQRouteDao

  companion object {
    @Volatile
    private var INSTANCE: ResQRouteDatabase? = null

    fun getDatabase(context: Context): ResQRouteDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          ResQRouteDatabase::class.java,
          "resq_route_database"
        )
          .fallbackToDestructiveMigration(true)
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
