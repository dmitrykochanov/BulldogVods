package com.dmko.bulldogvods.features.vods.presentation.recycler.videosources

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.databinding.ListItemVideoSourceBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem

class VideoSourceItemsAdapter(
    private val onVideoSourceClickListener: (String) -> Unit
) : ListAdapter<VideoSourceItem, VideoSourceItemViewHolder>(VideoSourceItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoSourceItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemVideoSourceBinding.inflate(inflater, parent, false)
        return VideoSourceItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoSourceItemViewHolder, position: Int) {
        val videoSourceItem = requireNotNull(getItem(position))
        holder.bindVideoSourceItemItem(videoSourceItem)
        holder.setOnVideoSourceClickListener { onVideoSourceClickListener.invoke(videoSourceItem.url) }
    }
}
