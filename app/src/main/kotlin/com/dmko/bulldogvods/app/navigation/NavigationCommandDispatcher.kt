package com.dmko.bulldogvods.app.navigation

interface NavigationCommandDispatcher {

    fun dispatch(command: NavigationCommand)
}
