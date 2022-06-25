package com.dmko.bulldogvods.features.chat.domain.entities

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class ChatReplayConfig(
    val pageSize: Duration = 20.seconds,
    val historySize: Duration = 3.minutes,
    val initialPreloadOffset: Duration = 20.seconds,
    val prefetchDelay: Duration = 5.seconds,
    val playbackPositionOffset: Duration = 6.5.seconds
)
