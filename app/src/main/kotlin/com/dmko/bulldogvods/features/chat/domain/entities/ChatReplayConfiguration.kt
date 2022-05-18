package com.dmko.bulldogvods.features.chat.domain.entities

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ChatReplayConfiguration(
    val pageSize: Int = 50,
    val historySize: Int = 200,
    val prefetchDelay: Duration = 5.seconds,
    val initialPreloadOffset: Duration = 5.seconds
)
