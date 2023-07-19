package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemVodBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem

class VodItemVodsSectionViewHolder(
    private val binding: ListItemVodBinding,
    private val imageLoader: ImageLoader
) : VodItemViewHolder<VodItem.VodsSection>(binding.root) {

    override fun bindVodItem(vodItem: VodItem.VodsSection) {
    }

    fun setOnVodClickListener(onVodClickListener: () -> Unit) {

    }
}
