package com.dmko.bulldogvods.app.common.extensions

inline fun <T, R : Comparable<R>> Iterable<T>.maxByOrThrow(selector: (T) -> R): T {
    return requireNotNull(maxByOrNull(selector))
}
