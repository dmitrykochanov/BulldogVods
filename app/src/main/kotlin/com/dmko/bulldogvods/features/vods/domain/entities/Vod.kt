package com.dmko.bulldogvods.features.vods.domain.entities

data class Vod(
    val id: String,
    val title: String,
    val recordedAtMillis: Long,
    val state: VodState,
    val chapters: List<VodChapter>,
    val videoSources: List<VodVideoSource>
)
