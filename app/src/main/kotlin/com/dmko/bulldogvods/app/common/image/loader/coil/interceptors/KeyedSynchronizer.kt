package com.dmko.bulldogvods.app.common.image.loader.coil.interceptors

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class KeyedSynchronizer<Key : Any> {

    private val keyBasedMutexes: MutableMap<Key, MutexEntry> = mutableMapOf()

    private val mapLock = ReentrantLock()

    suspend fun <T> synchronizedFor(key: Key, action: suspend () -> T): T {
        return getMutex(key).withLock {
            try {
                action()
            } finally {
                removeMutex(key)
            }
        }
    }

    private fun getMutex(key: Key): Mutex {
        mapLock.withLock {
            val mutexEntry = keyBasedMutexes[key] ?: MutexEntry(Mutex(), 0)
            mutexEntry.counter++
            if (keyBasedMutexes[key] == null) {
                keyBasedMutexes[key] = mutexEntry
            }
            return mutexEntry.mutex
        }
    }

    private fun removeMutex(key: Key) {
        mapLock.withLock {
            val mutexEntry = keyBasedMutexes[key] ?: return
            mutexEntry.counter--
            if (mutexEntry.counter == 0) {
                keyBasedMutexes.remove(key)
            }
        }
    }

    private class MutexEntry(
        val mutex: Mutex,
        var counter: Int
    )
}
