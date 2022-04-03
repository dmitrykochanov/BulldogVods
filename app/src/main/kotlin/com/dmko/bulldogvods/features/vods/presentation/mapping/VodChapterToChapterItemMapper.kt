package com.dmko.bulldogvods.features.vods.presentation.mapping

import com.dmko.bulldogvods.features.vods.domain.entities.VodChapter
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem
import com.dmko.bulldogvods.features.vods.presentation.formatting.DurationFormat
import javax.inject.Inject

class VodChapterToChapterItemMapper @Inject constructor(
    private val durationFormat: DurationFormat
) {

    fun map(vodChapter: VodChapter): ChapterItem {
        return ChapterItem(
            gameThumbnailUrl = vodChapter.gameThumbnailUrl,
            gameName = vodChapter.gameName,
            length = durationFormat.format(vodChapter.length),
            startOffset = vodChapter.startOffset
        )
    }
}
