package com.dmko.bulldogvods.features.vods.domain.selector

import com.dmko.bulldogvods.app.common.maxByOrThrow
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource.Quality.Adaptive
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource.Quality.Static
import javax.inject.Inject

class DefaultVideoSourceSelector @Inject constructor() {

    fun selectDefaultVideoSource(sources: List<VideoSource>): VideoSource {
        return sources.maxByOrThrow { source ->
            when (source.quality) {
                is Static -> source.quality.height
                is Adaptive -> Int.MAX_VALUE
            }
        }
    }
}
