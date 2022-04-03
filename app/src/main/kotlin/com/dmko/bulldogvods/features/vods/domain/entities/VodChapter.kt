package com.dmko.bulldogvods.features.vods.domain.entities

import kotlin.time.Duration

data class VodChapter(
    val gameName: String,
    val gameThumbnailUrl: String,
    val startOffset: Duration,
    val length: Duration
)
