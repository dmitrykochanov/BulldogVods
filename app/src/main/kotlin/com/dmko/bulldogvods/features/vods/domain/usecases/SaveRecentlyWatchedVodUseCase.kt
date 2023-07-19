package com.dmko.bulldogvods.features.vods.domain.usecases

import com.dmko.bulldogvods.features.vods.data.database.datasource.DatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.RecentlyWatchedVod
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveRecentlyWatchedVodUseCase @Inject constructor(
    private val databaseVodsDataSource: DatabaseVodsDataSource
) {

    fun execute(vodId: String): Completable {
        return databaseVodsDataSource.observeRecentlyWatchedVods()
            .firstOrError()
            .map { recentlyWatchedVods -> addVodToRecentlyWatched(vodId, recentlyWatchedVods) }
            .flatMapCompletable { updatedVods -> databaseVodsDataSource.saveRecentlyWatchedVods(updatedVods) }
    }

    private fun addVodToRecentlyWatched(
        vodId: String,
        recentlyWatchedVods: List<RecentlyWatchedVod>
    ): List<RecentlyWatchedVod> {
        val vodToAdd = RecentlyWatchedVod(vodId, System.currentTimeMillis())
        val updatedVods = recentlyWatchedVods.filter { it.vodId != vodId } + vodToAdd
        return updatedVods.sortedBy { it.watchedAt }.take(MAX_RECENTLY_WATCHED_VODS_COUNT)
    }

    private companion object {

        private const val MAX_RECENTLY_WATCHED_VODS_COUNT = 5
    }
}
