package com.dmko.bulldogvods.features.vods.domain.entities

data class Vod(
    val id: String,
    val title: String,
    val startedAtMillis: Long,
    val endedAtMillis: Long?,
    val state: VodState,
    val chapters: List<VodChapter>,
    val videoSources: List<VideoSource>
)
