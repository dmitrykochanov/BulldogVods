package com.dmko.bulldogvods.app.navigation

import com.zhuinden.eventemitter.EventEmitter
import com.zhuinden.eventemitter.EventSource

class NavigationCoordinator : NavigationCommandDispatcher, NavigationCommandSource {

    private val emitter = EventEmitter<NavigationCommand>()
    override val source: EventSource<NavigationCommand> = emitter

    override fun dispatch(command: NavigationCommand) {
        emitter.emit(command)
    }
}
