package com.dmko.bulldogvods.features.vods.domain.entities

import kotlin.time.Duration


sealed class VodState {

    object Live : VodState()

    data class Ready(
        val length: Duration
    ) : VodState()

    data class Processing(
        val length: Duration
    ) : VodState()

    object Unknown : VodState()
}
