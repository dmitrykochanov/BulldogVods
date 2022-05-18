package com.dmko.bulldogvods.app.navigation

import kotlin.time.Duration

sealed class NavigationCommand {

    data class Vod(
        val vodId: String,
        val startOffset: Duration = Duration.ZERO
    ) : NavigationCommand()

    object SearchVods : NavigationCommand()

    data class ChapterChooser(
        val vodId: String
    ) : NavigationCommand()

    data class VodPlaybackSettings(
        val vodId: String,
        val selectedVideoSourceUrl: String
    ) : NavigationCommand()

    object ThemeChooser : NavigationCommand()

    object Back : NavigationCommand()
}
