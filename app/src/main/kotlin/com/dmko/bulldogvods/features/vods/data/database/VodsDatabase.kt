package com.dmko.bulldogvods.features.vods.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dmko.bulldogvods.features.vods.data.database.dao.VodsDao
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.RecentlyWatchedVodEntity
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity

@Database(
    entities = [VodPlaybackPositionEntity::class, RecentlyWatchedVodEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class VodsDatabase : RoomDatabase() {

    abstract fun vodsDao(): VodsDao
}
