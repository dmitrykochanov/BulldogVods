package com.dmko.bulldogvods.features.chat.domain.entities

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ChatReplayConfig(
    val pageSize: Int = 100,
    val historySize: Int = 200,
    val prefetchDelay: Duration = 5.seconds,
    val initialPreloadOffset: Duration = 20.seconds,
    val playbackPositionOffset: Duration = 12.seconds
)
