package com.dmko.bulldogvods.app.navigation

import com.zhuinden.eventemitter.EventSource

interface NavigationEventSource {

    val source: EventSource<NavigationEvent>
}
