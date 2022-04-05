package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.VodVideoSource
import javax.inject.Inject

class VodSchemaToVodVideoSourcesMapper @Inject constructor() {

    fun map(vodSchema: VodSchema): List<VodVideoSource> {
        val sources = mutableListOf<VodVideoSource>()
        sources += VodVideoSource(
            quality = VodVideoSource.Quality.Adaptive,
            url = SOURCE_URL_MASTER.format(vodSchema.id),
            isReady = vodSchema.variants.any { it.ready }
        )
        sources += vodSchema.variants.map { vodVariant ->
            VodVideoSource(
                quality = VodVideoSource.Quality.Static(
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