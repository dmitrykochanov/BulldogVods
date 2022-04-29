package com.dmko.bulldogvods.features.vods.presentation.recycler.videosources

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.databinding.ListItemVideoSourceBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem

class VideoSourceItemViewHolder(
    private val binding: ListItemVideoSourceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindVideoSourceItemItem(videoSourceItem: VideoSourceItem) {
        binding.imageCheck.isInvisible = !videoSourceItem.isSelected
        binding.textName.text = videoSourceItem.name
    }

    fun setOnVideoSourceClickListener(onVideoSourceClickListener: () -> Unit) {
        binding.root.setOnClickListener { onVideoSourceClickListener.invoke() }
    }
}
