package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemVodBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem

class VodItemsAdapter(
    private val imageLoader: ImageLoader,
    private val onVodClickListener: (String) -> Unit,
    private val onVodChaptersClickListener: (String) -> Unit
) : PagingDataAdapter<VodItem, VodItemViewHolder>(VodItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VodItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemVodBinding.inflate(inflater, parent, false)
        return VodItemViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: VodItemViewHolder, position: Int) {
        val vodItem = requireNotNull(getItem(position))
        holder.bindVodItem(vodItem)
        holder.setOnVodClickListener { onVodClickListener.invoke(vodItem.id) }
        holder.setOnVodChaptersClickListener { onVodChaptersClickListener.invoke(vodItem.id) }
    }
}
