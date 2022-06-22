package com.dmko.bulldogvods.features.vods.presentation.mapping

import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem
import javax.inject.Inject

class VideoSourceToVideoSourceItemMapper @Inject constructor(
    private val videoSourceToVideoSourceNameMapper: VideoSourceToVideoSourceNameMapper
) {

    fun map(selectedVideoSourceUrl: String, videoSource: VideoSource): VideoSourceItem {
        return VideoSourceItem(
            isSelected = selectedVideoSourceUrl == videoSource.url,
            name = videoSourceToVideoSourceNameMapper.map(videoSource),
            url = videoSource.url
        )
    }
}
