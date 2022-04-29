package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import javax.inject.Inject

class VodSchemaToVideoSourcesMapper @Inject constructor() {

    fun map(vodSchema: VodSchema): List<VideoSource> {
        val sources = mutableListOf<VideoSource>()
        sources += VideoSource(
            quality = VideoSource.Quality.Adaptive,
            url = SOURCE_URL_MASTER.format(vodSchema.id),
            isReady = vodSchema.variants.any { it.ready }
        )
        sources += vodSchema.variants.map { vodVariant ->
            VideoSource(
                quality = VideoSource.Quality.Static(
                    width = vodVariant.width,
                    height = vodVariant.height,
                    fps = vodVariant.fps
                ),
                url = SOURCE_URL_PLAYLIST.format(vodSchema.id, vodVariant.name),
                isReady = vodVariant.ready
            )
        }
        return sources
    }

    private companion object {

        private const val SOURCE_URL_MASTER = "https://cdn.admiralbulldog.live/vods/%s/master.m3u8"
        private const val SOURCE_URL_PLAYLIST = "https://cdn.admiralbulldog.live/vods/%s/%s/playlist.m3u8"
    }
}