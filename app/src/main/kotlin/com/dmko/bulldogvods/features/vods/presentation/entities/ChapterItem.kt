package com.dmko.bulldogvods.features.vods.presentation.entities

import kotlin.time.Duration

data class ChapterItem(
    val gameThumbnailUrl: String,
    val gameName: String,
    val length: String,
    val startOffset: Duration
)
