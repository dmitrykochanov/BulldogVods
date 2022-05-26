package com.dmko.bulldogvods.features.vods.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface VodsDao {

    @Query("SELECT playback_position FROM vod_playback_position WHERE vod_id = :vodId")
    fun getVodPlaybackPosition(vodId: String): Flowable<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVodPlaybackPosition(vodPlaybackPositionEntity: VodPlaybackPositionEntity): Completable
}
