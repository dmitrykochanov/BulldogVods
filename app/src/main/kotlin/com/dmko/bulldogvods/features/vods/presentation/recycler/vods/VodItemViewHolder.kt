package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem

abstract class VodItemViewHolder<ItemType : VodItem>(
    itemView: View
) : ViewHolder(itemView) {

    abstract fun bindVodItem(vodItem: ItemType)
}
