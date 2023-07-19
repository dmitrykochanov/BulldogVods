package com.dmko.bulldogvods.features.vods.presentation.entities

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

sealed class VodItem {

    data class Vod(
        val id: String,
        val thumbnailUrl: String,
        val length: String?,
        val recordedAt: String,
        val playbackPercentage: Int,
        val gameThumbnailUrl: String,
        val title: String,
        val stateBadge: StateBadge?,
        val chaptersSection: ChaptersSection
    ) : VodItem() {

        data class StateBadge(
            @ColorRes val backgroundColor: Int,
            @StringRes val text: Int
        )

        sealed class ChaptersSection {

            object NoChapters : ChaptersSection()

            data class SingleChapter(
                val gameName: String
            ) : ChaptersSection()

            data class MultipleChapters(
                val chaptersCount: Int
            ) : ChaptersSection()
        }
    }

    data class VodsSection(
        @StringRes val title: Int,
        val vods: List<Vod>
    ) : VodItem()
}
