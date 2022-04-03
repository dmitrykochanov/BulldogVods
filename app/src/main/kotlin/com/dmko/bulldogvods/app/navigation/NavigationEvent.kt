package com.dmko.bulldogvods.app.navigation

import kotlin.time.Duration

sealed class NavigationEvent {

    data class VodPlayback(
        val vodId: String,
        val startOffset: Duration = Duration.ZERO
    ) : NavigationEvent()

    data class ChapterChooser(
        val vodId: String
    ) : NavigationEvent()
}
