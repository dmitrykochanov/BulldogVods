package com.dmko.bulldogvods.app.navigation

import kotlin.time.Duration

sealed class NavigationEvent {

    object Back : NavigationEvent()

    data class VodPlayback(
        val vodId: String,
        val startOffset: Duration = Duration.ZERO
    ) : NavigationEvent()

    object SearchVods : NavigationEvent()

    data class ChapterChooser(
        val vodId: String
    ) : NavigationEvent()

    data class VodPlaybackSettings(
        val vodId: String,
        val selectedVideoSourceUrl: String
    ) : NavigationEvent()
}
