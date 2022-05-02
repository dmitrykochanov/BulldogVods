package com.dmko.bulldogvods.app.navigation

import com.zhuinden.eventemitter.EventSource

interface NavigationCommandSource {

    val source: EventSource<NavigationCommand>
}
