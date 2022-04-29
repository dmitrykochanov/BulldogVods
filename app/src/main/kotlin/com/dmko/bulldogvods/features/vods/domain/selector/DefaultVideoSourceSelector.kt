package com.dmko.bulldogvods.features.vods.domain.selector

import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import javax.inject.Inject

class DefaultVideoSourceSelector @Inject constructor() {

    fun selectDefaultVideoSource(sources: List<VideoSource>): VideoSource {
        return sources.first { it.quality is VideoSource.Quality.Adaptive }
    }
}
