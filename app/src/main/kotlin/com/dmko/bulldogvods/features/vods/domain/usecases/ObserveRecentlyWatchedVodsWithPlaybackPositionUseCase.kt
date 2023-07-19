package com.dmko.bulldogvods.features.vods.domain.usecases

import com.dmko.bulldogvods.features.vods.data.database.datasource.DatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.RecentlyWatchedVod
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ObserveRecentlyWatchedVodsWithPlaybackPositionUseCase @Inject constructor(
    private val databaseVodsDataSource: DatabaseVodsDataSource,
    private val networkVodsDataSource: NetworkVodsDataSource,
    private val getVodsPlaybackPositionsUseCase: GetVodsPlaybackPositionsUseCase
) {

    fun execute(forceRefresh: Boolean): Flowable<List<VodWithPlaybackPosition>> {
        return databaseVodsDataSource.observeRecentlyWatchedVods()
            .flatMapSingle { recentlyWatchedVods -> getVods(recentlyWatchedVods, forceRefresh) }
            .flatMapSingle(getVodsPlaybackPositionsUseCase::execute)
    }

    private fun getVods(recentlyWatchedVods: List<RecentlyWatchedVod>, forceRefresh: Boolean): Single<List<Vod>> {
        val loadVodSingles = recentlyWatchedVods.map { recentlyWatchedVod ->
            networkVodsDataSource.getVod(recentlyWatchedVod.vodId, forceRefresh)
        }
        return Single.zip(loadVodSingles) { vodsArray -> vodsArray.toList().filterIsInstance<Vod>() }
    }
}
