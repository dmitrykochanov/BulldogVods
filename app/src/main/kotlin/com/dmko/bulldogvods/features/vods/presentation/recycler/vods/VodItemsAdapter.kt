package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemVodBinding
import com.dmko.bulldogvods.databinding.ListItemVodsSectionBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem

class VodItemsAdapter(
    private val imageLoader: ImageLoader,
    private val onVodClickListener: (String) -> Unit,
    private val onVodChaptersClickListener: (String) -> Unit
) : PagingDataAdapter<VodItem, VodItemViewHolder<VodItem>>(VodItemDiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        return when (requireNotNull(getItem(position))) {
            is VodItem.Vod -> VIEW_TYPE_VOD
            is VodItem.VodsSection -> VIEW_TYPE_VODS_SECTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VodItemViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_VOD -> {
                val binding = ListItemVodBinding.inflate(inflater, parent, false)
                VodItemVodViewHolder(binding, imageLoader)
            }
            VIEW_TYPE_VODS_SECTION -> {
                val binding = ListItemVodsSectionBinding.inflate(inflater, parent, false)
                VodItemVodsSectionViewHolder(binding, imageLoader)
            }
            else -> throw IllegalStateException("Unknown view type $viewType")
        }

    }

    override fun onBindViewHolder(holder: VodItemViewHolder<*>, position: Int) {
        holder.bindVodItem(requireNotNull(getItem(position)))
    }

    override fun onBindViewHolder(holder: VodItemVodViewHolder, position: Int) {
        val vodItem = requireNotNull(getItem(position))
        holder.bindVod(vodItem)
        holder.setOnVodClickListener { onVodClickListener.invoke(vodItem.id) }
        holder.setOnVodChaptersClickListener { onVodChaptersClickListener.invoke(vodItem.id) }
    }

    private companion object {

        private const val VIEW_TYPE_VOD = 0
        private const val VIEW_TYPE_VODS_SECTION = 1
    }
}
