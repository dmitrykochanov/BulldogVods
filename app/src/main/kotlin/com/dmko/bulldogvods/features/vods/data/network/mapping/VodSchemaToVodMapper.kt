package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import javax.inject.Inject

class VodSchemaToVodMapper @Inject constructor(
    private val vodSchemaToVodStateMapper: VodSchemaToVodStateMapper,
    private val vodSchemaToVodChaptersMapper: VodSchemaToVodChaptersMapper,
    private val vodSchemaToVodVideoSourcesMapper: VodSchemaToVodVideoSourcesMapper,
    private val dateStringToTimestampMapper: DateStringToTimestampMapper
) {

    fun map(vodSchema: VodSchema): Vod {
        return Vod(
            id = vodSchema.id as String,
            title = vodSchema.title,
            recordedAtMillis = dateStringToTimestampMapper.map(vodSchema.started_at as String),
            state = vodSchemaToVodStateMapper.map(vodSchema),
            chapters = vodSchemaToVodChaptersMapper.map(vodSchema),
            videoSources = vodSchemaToVodVideoSourcesMapper.map(vodSchema),
        )
    }
}
