package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.VodChapter
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class VodSchemaToVodChaptersMapper @Inject constructor(
    private val dateStringToTimestampMapper: DateStringToTimestampMapper
) {

    fun map(vodSchema: VodSchema): List<VodChapter> {
        val vodStartedAtMillis = dateStringToTimestampMapper.map(vodSchema.started_at as String)
        val vodEndedAtMillis = (vodSchema.ended_at as String?)?.let(dateStringToTimestampMapper::map)
        val chapters = mutableListOf<VodChapter>()
        for (i in vodSchema.categories.indices) {
            val category = vodSchema.categories[i]
            val timestamp = dateStringToTimestampMapper.map(category.timestamp as String)
            val nextCategory = vodSchema.categories.getOrNull(i + 1)
            val nextCategoryTimestamp = (nextCategory?.timestamp as String?)?.let(dateStringToTimestampMapper::map)
            val nextTimestamp = nextCategoryTimestamp ?: vodEndedAtMillis
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
}