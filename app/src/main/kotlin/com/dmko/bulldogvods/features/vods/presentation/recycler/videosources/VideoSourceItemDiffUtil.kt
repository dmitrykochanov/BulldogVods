package com.dmko.bulldogvods.features.vods.presentation.recycler.videosources

import androidx.recyclerview.widget.DiffUtil
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem

class VideoSourceItemDiffUtil : DiffUtil.ItemCallback<VideoSourceItem>() {

    override fun areItemsTheSame(oldItem: VideoSourceItem, newItem: VideoSourceItem): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: VideoSourceItem, newItem: VideoSourceItem): Boolean {
        return oldItem == newItem
    }
}
