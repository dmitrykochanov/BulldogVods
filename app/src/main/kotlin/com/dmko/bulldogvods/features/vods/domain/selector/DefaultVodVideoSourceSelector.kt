package com.dmko.bulldogvods.features.vods.domain.selector

import com.dmko.bulldogvods.features.vods.domain.entities.VodVideoSource
import javax.inject.Inject

class DefaultVodVideoSourceSelector @Inject constructor() {

    fun selectDefaultVideoSource(sources: List<VodVideoSource>): VodVideoSource {
        return sources.first { it.quality is VodVideoSource.Quality.Adaptive }
    }
}
