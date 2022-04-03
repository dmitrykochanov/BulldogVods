package com.dmko.bulldogvods.features.vods.presentation.entities

sealed class ChapterItemsState {

    object Loading : ChapterItemsState()

    data class Data(
        val chapterItems: List<ChapterItem>
    ) : ChapterItemsState()

    object Error : ChapterItemsState()
}
