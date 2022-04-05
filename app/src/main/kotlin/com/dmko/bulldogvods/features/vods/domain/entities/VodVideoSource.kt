package com.dmko.bulldogvods.features.vods.domain.entities

data class VodVideoSource(
    val quality: Quality,
    val url: String,
    val isReady: Boolean
) {

    sealed class Quality {

        data class Static(
            val width: Int,
            val height: Int,
            val fps: Int
        ) : Quality()

        object Adaptive : Quality()
    }
}
