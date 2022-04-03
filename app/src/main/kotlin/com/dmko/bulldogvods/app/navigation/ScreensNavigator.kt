package com.dmko.bulldogvods.app.navigation

import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class ScreensNavigator : NavigationEventDispatcher, NavigationEventSource {

    private val emitter = EventEmitter<NavigationEvent>()
    override val source: EventSource<NavigationEvent> = emitter

    override fun dispatch(event: NavigationEvent) {
        emitter.emit(event)
    }
}
