package com.dmko.bulldogvods.features.vods.data.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_watched_vod")
data class RecentlyWatchedVodEntity(
    @PrimaryKey @ColumnInfo(name = "vod_id") val vodId: String,
    @ColumnInfo(name = "watched_at") val watchedAt: Long
)
