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
                    val heightNamePart = "${videoSource.quality.height}p"
                    val fpsNamePart = if (videoSource.quality.fps == 60) {
                        "60"
                    } else {
                        ""
                    }
                    "$heightNamePart$fpsNamePart"
                }
                is Adaptive -> "Auto"
            },
            url = videoSource.url
        )
    }
}
