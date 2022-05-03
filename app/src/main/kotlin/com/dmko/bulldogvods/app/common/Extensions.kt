package com.dmko.bulldogvods.app.common

inline fun <T, R : Comparable<R>> Iterable<T>.maxByOrThrow(selector: (T) -> R): T {
    return requireNotNull(maxByOrNull(selector))
}
