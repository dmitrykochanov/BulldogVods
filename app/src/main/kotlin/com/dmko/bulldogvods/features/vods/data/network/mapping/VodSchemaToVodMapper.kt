package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodChapter
import com.dmko.bulldogvods.features.vods.domain.entities.VodState
import com.dmko.bulldogvods.features.vods.domain.entities.VodVideoSource
import java.time.OffsetDateTime
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import apollo.type.VodState as VodStateSchema

class VodSchemaToVodMapper @Inject constructor() {

    fun map(vodSchema: VodSchema): Vod {
        val startedAtMillis = parseDate(vodSchema.started_at as String)
        val endedAtMillis = (vodSchema.ended_at as String?)?.let(::parseDate)
        return Vod(
            id = vodSchema.id as String,
            title = vodSchema.title,
            recordedAtMillis = startedAtMillis,
            state = mapVodState(startedAtMillis, endedAtMillis, vodSchema.state),
            chapters = mapVodChapters(startedAtMillis, endedAtMillis, vodSchema.categories),
            videoSources = vodSchema.variants.map(::mapVideoSource),
        )
    }

    private fun mapVodState(
        vodStartedAtMillis: Long,
        vodEndedAtMillis: Long?,
        vodStateSchema: VodStateSchema
    ): VodState {
        return when (vodStateSchema) {
            VodStateSchema.Live -> VodState.Live
            VodStateSchema.Ready -> {
                requireNotNull(vodEndedAtMillis)
                VodState.Ready(
                    length = (vodEndedAtMillis - vodStartedAtMillis).milliseconds
                )
            }
            VodStateSchema.Processing -> {
                requireNotNull(vodEndedAtMillis)
                VodState.Processing(
                    length = (vodEndedAtMillis - vodStartedAtMillis).milliseconds
                )
            }
            else -> VodState.Unknown
        }
    }

    private fun mapVodChapters(
        vodStartedAtMillis: Long,
        vodEndedAtMillis: Long?,
        categories: List<VodSchema.Category>
    ): List<VodChapter> {
        val chapters = mutableListOf<VodChapter>()
        for (i in categories.indices) {
            val category = categories[i]
            val timestamp = parseDate(category.timestamp as String)
            val nextCategory = categories.getOrNull(i + 1)
            val nextTimestamp = (nextCategory?.timestamp as String?)?.let(::parseDate) ?: vodEndedAtMillis
            chapters += VodChapter(
                gameName = category.name,
                gameThumbnailUrl = category.url,
                startOffset = (timestamp - vodStartedAtMillis).milliseconds,
                length = if (nextTimestamp != null) {
                    (nextTimestamp - timestamp).milliseconds
                } else {
                    Duration.ZERO
                }
            )
        }
        return chapters
    }

    private fun mapVideoSource(variant: VodSchema.Variant): VodVideoSource {
        return VodVideoSource(
            name = variant.name,
            width = variant.width,
            height = variant.height,
            fps = variant.fps
        )
    }

    private fun parseDate(date: String): Long {
        return OffsetDateTime.parse(date).toInstant().toEpochMilli()
    }
}
