package com.dmko.bulldogvods.features.vods.data.database.datasource

import com.dmko.bulldogvods.features.vods.data.database.dao.VodsDao
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class RoomDatabaseVodsDataSource @Inject constructor(
    private val vodsDao: VodsDao
) : DatabaseVodsDataSource {

    override fun getVodPlaybackPosition(vodId: String): Flowable<Long> {
        return vodsDao.getVodPlaybackPosition(vodId)
    }

    override fun saveVodPlaybackPosition(vodId: String, playbackPosition: Long): Completable {
        return vodsDao.saveVodPlaybackPosition(VodPlaybackPositionEntity(vodId, playbackPosition))
    }
}
