package com.dmko.bulldogvods.features.vods.domain.entities

data class VodWithPlaybackPosition(
    val vod: Vod,
    val playbackPosition: Long
)
