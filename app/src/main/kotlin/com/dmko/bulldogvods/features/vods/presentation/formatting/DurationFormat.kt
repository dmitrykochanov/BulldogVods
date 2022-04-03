package com.dmko.bulldogvods.features.vods.presentation.formatting

import javax.inject.Inject
import kotlin.time.Duration

class DurationFormat @Inject constructor() {

    fun format(duration: Duration): String {
        val hours = duration.inWholeHours
        val remainingMinutes = duration.inWholeMinutes % 60
        val remainingSeconds = duration.inWholeSeconds % 60 % 60
        return String.format("%01d:%02d:%02d", hours, remainingMinutes, remainingSeconds)
    }
}
