package com.dmko.bulldogvods.app.navigation

sealed class NavigationEvent {

    data class VodPlayback(
        val vodId: String
    ) : NavigationEvent()
}
