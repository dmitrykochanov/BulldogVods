package com.dmko.bulldogvods.app.navigation

import kotlin.time.Duration

sealed class NavigationCommand {

    data class Vod(
        val vodId: String,
        val startOffset: Duration? = null
    ) : NavigationCommand()

    object SearchVods : NavigationCommand()

    data class ChapterChooser(
        val vodId: String
    ) : NavigationCommand()

    data class VodSettings(
        val vodId: String,
        val selectedVideoSourceUrl: String
    ) : NavigationCommand()

    data class VideoSourceChooser(
        val vodId: String,
        val selectedVideoSourceUrl: String
    ) : NavigationCommand()

    object ChatPositionChooser : NavigationCommand()

    object ThemeChooser : NavigationCommand()

    object Back : NavigationCommand()
}
