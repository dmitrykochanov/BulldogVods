package com.dmko.bulldogvods.features.vods.presentation.mapping

import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodChapter
import com.dmko.bulldogvods.features.vods.domain.entities.VodState
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.Vod.ChaptersSection.*
import com.dmko.bulldogvods.features.vods.presentation.formatting.DurationFormat
import com.dmko.bulldogvods.features.vods.presentation.formatting.RelativeDateFormat
import javax.inject.Inject
import kotlin.math.roundToInt

class VodWithPlaybackPositionToVodItemMapper @Inject constructor(
    private val durationFormat: DurationFormat,
    private val relativeDateFormat: RelativeDateFormat
) {

    fun map(vodWithPlaybackPosition: VodWithPlaybackPosition): VodItem {
        val vod = vodWithPlaybackPosition.vod
        return VodItem.Vod(
            id = vod.id,
            thumbnailUrl = vod.thumbnailUrl ?: DEFAULT_VOD_THUMBNAIL_URL,
            length = mapVodStateToDuration(vod.state),
            recordedAt = relativeDateFormat.format(vod.startedAtMillis),
            playbackPercentage = getPlaybackPercentage(vod, vodWithPlaybackPosition.playbackPosition),
            gameThumbnailUrl = getLongestChapterGameThumbnail(vod.chapters) ?: DEFAULT_GAME_THUMBNAIL_URL,
            title = vod.title,
            stateBadge = mapVodStateToBadge(vod.state),
            chaptersSection = mapChaptersToSection(vod.chapters)
        )
    }

    private fun mapVodStateToDuration(vodState: VodState): String? {
        return when (vodState) {
            is VodState.Ready -> durationFormat.format(vodState.length)
            is VodState.Processing -> durationFormat.format(vodState.length)
            is VodState.Live, is VodState.Unknown -> null
        }
    }

    private fun getPlaybackPercentage(vod: Vod, playbackPosition: Long): Int {
        if (vod.endedAtMillis == null) return 0
        val vodDuration = vod.endedAtMillis - vod.startedAtMillis
        return (playbackPosition * 100.0 / vodDuration).roundToInt()
    }

    private fun getLongestChapterGameThumbnail(chapters: List<VodChapter>): String? {
        return chapters.maxByOrNull(VodChapter::length)?.gameThumbnailUrl
    }

    private fun mapVodStateToBadge(vodState: VodState): VodItem.Vod.StateBadge? {
        return when (vodState) {
            is VodState.Live -> VodItem.Vod.StateBadge(
                backgroundColor = R.color.red_70,
                text = R.string.vod_state_live
            )
            is VodState.Ready -> VodItem.Vod.StateBadge(
                backgroundColor = R.color.fruit_salad_70,
                text = R.string.vod_state_ready
            )
            is VodState.Processing -> VodItem.Vod.StateBadge(
                backgroundColor = R.color.clementine_70,
                text = R.string.vod_state_processing
            )
            is VodState.Unknown -> null
        }
    }

    private fun mapChaptersToSection(chapters: List<VodChapter>): VodItem.Vod.ChaptersSection {
        return when {
            chapters.isEmpty() -> NoChapters
            chapters.size == 1 -> SingleChapter(gameName = chapters.first().gameName)
            else -> MultipleChapters(chaptersCount = chapters.size)
        }
    }

    private companion object {

        private const val DEFAULT_VOD_THUMBNAIL_URL =
            "https://vods.admiralbulldog.live/assets/default-thumbnail.2162522e.webp"
        private const val DEFAULT_GAME_THUMBNAIL_URL = "https://static-cdn.jtvnw.net/ttv-boxart/66082-285x380.jpg"
    }
}
