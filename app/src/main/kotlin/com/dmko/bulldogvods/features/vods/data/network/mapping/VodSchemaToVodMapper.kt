package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import javax.inject.Inject

class VodSchemaToVodMapper @Inject constructor(
    private val vodSchemaToVodStateMapper: VodSchemaToVodStateMapper,
    private val vodSchemaToVodChaptersMapper: VodSchemaToVodChaptersMapper,
    private val vodSchemaToVideoSourcesMapper: VodSchemaToVideoSourcesMapper,
    private val dateStringToTimestampMapper: DateStringToTimestampMapper
) {

    fun map(vodSchema: VodSchema): Vod {
        return Vod(
            id = vodSchema.id as String,
            title = vodSchema.title,
            startedAtMillis = dateStringToTimestampMapper.map(vodSchema.started_at as String),
            endedAtMillis = (vodSchema.ended_at as String?)?.let(dateStringToTimestampMapper::map),
            state = vodSchemaToVodStateMapper.map(vodSchema),
            chapters = vodSchemaToVodChaptersMapper.map(vodSchema),
            videoSources = vodSchemaToVideoSourcesMapper.map(vodSchema),
        )
    }
}
