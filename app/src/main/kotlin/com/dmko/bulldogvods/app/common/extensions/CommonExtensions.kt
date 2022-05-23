package com.dmko.bulldogvods.app.common.extensions

import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt

@ColorInt
fun String.toColorIntOrNull(): Int? {
    return try {
        toColorInt()
    } catch (e: Throwable) {
        null
    }
}
