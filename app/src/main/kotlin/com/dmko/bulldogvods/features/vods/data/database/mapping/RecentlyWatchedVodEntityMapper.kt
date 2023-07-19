package com.dmko.bulldogvods.features.vods.data.database.mapping

import com.dmko.bulldogvods.features.vods.data.database.dao.entities.RecentlyWatchedVodEntity
import com.dmko.bulldogvods.features.vods.domain.entities.RecentlyWatchedVod
import javax.inject.Inject

class RecentlyWatchedVodEntityMapper @Inject constructor() {

    fun mapEntity(recentlyWatchedVodEntity: RecentlyWatchedVodEntity): RecentlyWatchedVod {
        return RecentlyWatchedVod(
            vodId = recentlyWatchedVodEntity.vodId,
            watchedAt = recentlyWatchedVodEntity.watchedAt
        )
    }

    fun mapToEntity(recentlyWatchedVod: RecentlyWatchedVod): RecentlyWatchedVodEntity {
        return RecentlyWatchedVodEntity(
            vodId = recentlyWatchedVod.vodId,
            watchedAt = recentlyWatchedVod.watchedAt
        )
    }
}
