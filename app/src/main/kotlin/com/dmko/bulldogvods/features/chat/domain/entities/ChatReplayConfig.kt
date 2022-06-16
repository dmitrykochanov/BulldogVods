package com.dmko.bulldogvods.features.chat.domain.entities

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ChatReplayConfig(
    val pageSize: Duration = 20.seconds,
    val historySize: Duration = 60.seconds,
    val initialPreloadOffset: Duration = 10.seconds,
    val prefetchDelay: Duration = 5.seconds,
    val playbackPositionOffset: Duration = 6.5.seconds
)
