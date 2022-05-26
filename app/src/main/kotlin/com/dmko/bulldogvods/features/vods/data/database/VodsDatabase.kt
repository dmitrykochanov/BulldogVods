package com.dmko.bulldogvods.features.vods.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dmko.bulldogvods.features.vods.data.database.dao.VodsDao
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity

@Database(entities = [VodPlaybackPositionEntity::class], version = 1)
abstract class VodsDatabase : RoomDatabase() {

    abstract fun vodsDao(): VodsDao
}
