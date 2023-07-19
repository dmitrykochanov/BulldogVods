package com.dmko.bulldogvods.features.vods.domain.usecases

import com.dmko.bulldogvods.features.vods.data.database.datasource.DatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodPlaybackPosition
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetVodsPlaybackPositionsUseCase @Inject constructor(
    private val databaseVodsDataSource: DatabaseVodsDataSource
) {

    fun execute(vods: List<Vod>): Single<List<VodWithPlaybackPosition>> {
        val vodIds = vods.map(Vod::id)
        return databaseVodsDataSource.getVodPlaybackPositions(vodIds)
            .map { playbackPositions -> combineVodsWithPlaybackPositions(vods, playbackPositions) }
    }

    private fun combineVodsWithPlaybackPositions(
        vods: List<Vod>,
        playbackPositions: List<VodPlaybackPosition>
    ): List<VodWithPlaybackPosition> {
        return vods.map { vod ->
            VodWithPlaybackPosition(
                vod = vod,
                playbackPosition = playbackPositions.firstOrNull { it.vodId == vod.id }?.playbackPosition ?: 0
            )
        }
    }
}
