package com.dmko.bulldogvods.features.vods.presentation.recycler.chapters

import androidx.recyclerview.widget.DiffUtil
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem

class ChapterItemDiffUtil : DiffUtil.ItemCallback<ChapterItem>() {

    override fun areItemsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem.startOffset == newItem.startOffset
    }

    override fun areContentsTheSame(oldItem: ChapterItem, newItem: ChapterItem): Boolean {
        return oldItem == newItem
    }
}
