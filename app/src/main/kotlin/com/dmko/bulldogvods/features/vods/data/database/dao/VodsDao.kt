package com.dmko.bulldogvods.features.vods.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface VodsDao {

    @Query("SELECT playback_position FROM vod_playback_position WHERE vod_id = :vodId")
    fun observeVodPlaybackPosition(vodId: String): Flowable<Long>

    @Query("SELECT * FROM vod_playback_position WHERE vod_id IN (:vodIds)")
    fun getVodPlaybackPositions(vodIds: List<String>): Single<List<VodPlaybackPositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveVodPlaybackPosition(vodPlaybackPositionEntity: VodPlaybackPositionEntity): Completable
}
