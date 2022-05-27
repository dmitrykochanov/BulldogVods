package com.dmko.bulldogvods.features.vods.data.database.mapping

import com.dmko.bulldogvods.features.vods.data.database.dao.entities.VodPlaybackPositionEntity
import com.dmko.bulldogvods.features.vods.domain.entities.VodPlaybackPosition
import javax.inject.Inject

class PlaybackPositionEntityToPlaybackPositionMapper @Inject constructor() {

    fun map(playbackPositionEntity: VodPlaybackPositionEntity): VodPlaybackPosition {
        return VodPlaybackPosition(
            vodId = playbackPositionEntity.vodId,
            playbackPosition = playbackPositionEntity.playbackPosition
        )
    }
}
