package com.dmko.bulldogvods.features.vods.presentation.mapping

import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import javax.inject.Inject

class VideoSourceToVideoSourceNameMapper @Inject constructor() {

    fun map(videoSource: VideoSource): String {
        return when (videoSource.quality) {
            is VideoSource.Quality.Static -> {
                val heightNamePart = "${videoSource.quality.height}$HEIGHT_POSTFIX"
                val fpsNamePart = videoSource.quality.fps.takeIf { it == FPS_TO_SHOW }?.toString().orEmpty()
                "$heightNamePart$fpsNamePart"
            }
            is VideoSource.Quality.Adaptive -> ADAPTIVE_QUALITY_NAME
        }
    }

    private companion object {

        private const val ADAPTIVE_QUALITY_NAME = "Auto"
        private const val HEIGHT_POSTFIX = "p"
        private const val FPS_TO_SHOW = 60
    }
}
