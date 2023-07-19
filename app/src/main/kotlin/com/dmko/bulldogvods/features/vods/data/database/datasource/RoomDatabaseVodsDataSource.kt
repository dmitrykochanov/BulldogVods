package com.dmko.bulldogvods.features.vods.data.database.datasource

import com.dmko.bulldogvods.features.vods.data.database.dao.VodsDao
import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity
import com.dmko.bulldogvods.features.vods.data.database.mapping.PlaybackPositionEntityToPlaybackPositionMapper
import com.dmko.bulldogvods.features.vods.data.database.mapping.RecentlyWatchedVodEntityMapper
import com.dmko.bulldogvods.features.vods.domain.entities.RecentlyWatchedVod
import com.dmko.bulldogvods.features.vods.domain.entities.VodPlaybackPosition
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RoomDatabaseVodsDataSource @Inject constructor(
    private val vodsDao: VodsDao,
    private val playbackPositionEntityToPlaybackPositionMapper: PlaybackPositionEntityToPlaybackPositionMapper,
    private val recentlyWatchedVodEntityMapper: RecentlyWatchedVodEntityMapper
) : DatabaseVodsDataSource {

    override fun observeVodPlaybackPosition(vodId: String): Flowable<Long> {
        return vodsDao.observeVodPlaybackPosition(vodId)
    }

    override fun getVodPlaybackPositions(vodIds: List<String>): Single<List<VodPlaybackPosition>> {
        return vodsDao.getVodPlaybackPositions(vodIds)
            .map { entities -> entities.map(playbackPositionEntityToPlaybackPositionMapper::map) }
    }

    override fun saveVodPlaybackPosition(vodId: String, playbackPosition: Long): Completable {
        return vodsDao.saveVodPlaybackPosition(VodPlaybackPositionEntity(vodId, playbackPosition))
    }

    override fun observeRecentlyWatchedVods(): Flowable<List<RecentlyWatchedVod>> {
        return vodsDao.observeRecentlyWatchedVods()
            .map { entities -> entities.map(recentlyWatchedVodEntityMapper::mapEntity) }
    }

    override fun saveRecentlyWatchedVods(recentlyWatchedVods: List<RecentlyWatchedVod>): Completable {
        val entities = recentlyWatchedVods.map(recentlyWatchedVodEntityMapper::mapToEntity)
        return vodsDao.saveRecentlyWatchedVods(entities)
    }
}
