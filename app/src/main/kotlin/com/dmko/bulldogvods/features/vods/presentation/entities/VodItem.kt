package com.dmko.bulldogvods.features.vods.presentation.entities

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class VodItem(
    val id: String,
    val thumbnailUrl: String,
    val length: String?,
    val recordedAt: String,
    val gameThumbnailUrl: String,
    val title: String,
    val stateBadge: StateBadge?,
    val chaptersSection: ChaptersSection
) {

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
