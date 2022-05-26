package com.dmko.bulldogvods.features.vods.data.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vod_playback_position")
data class VodPlaybackPositionEntity(
    @PrimaryKey @ColumnInfo(name = "vod_id") val vodId: String,
    @ColumnInfo(name = "playback_position") val playbackPosition: Long
)
