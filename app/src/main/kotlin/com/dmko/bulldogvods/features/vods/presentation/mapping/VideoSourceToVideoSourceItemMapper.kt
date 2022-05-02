package com.dmko.bulldogvods.features.vods.presentation.mapping

import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource.Quality.Adaptive
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource.Quality.Static
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem
import javax.inject.Inject

class VideoSourceToVideoSourceItemMapper @Inject constructor() {

    fun map(selectedVideoSourceUrl: String, videoSource: VideoSource): VideoSourceItem {
        return VideoSourceItem(
            isSelected = selectedVideoSourceUrl == videoSource.url,
            name = when (videoSource.quality) {
                is Static -> {
                    val heightNamePart = "${videoSource.quality.height}$HEIGHT_POSTFIX"
                    val fpsNamePart = videoSource.quality.fps.takeIf { it == FPS_TO_SHOW }?.toString().orEmpty()
                    "$heightNamePart$fpsNamePart"
                }
                is Adaptive -> ADAPTIVE_QUALITY_NAME
            },
            url = videoSource.url
        )
    }

    private companion object {

        private const val ADAPTIVE_QUALITY_NAME = "Auto"
        private const val HEIGHT_POSTFIX = "p"
        private const val FPS_TO_SHOW = 60
    }
}
