package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import androidx.recyclerview.widget.DiffUtil
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem

class VodItemDiffUtil : DiffUtil.ItemCallback<VodItem>() {

    override fun areItemsTheSame(oldItem: VodItem, newItem: VodItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VodItem, newItem: VodItem): Boolean {
        return oldItem == newItem
    }
}
