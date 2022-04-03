package com.dmko.bulldogvods.features.vods.presentation.recycler.chapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChapterBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem
import kotlin.time.Duration

class ChapterItemsAdapter(
    private val imageLoader: ImageLoader,
    private val onChapterClickListener: (Duration) -> Unit
) : ListAdapter<ChapterItem, ChapterItemViewHolder>(ChapterItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChapterBinding.inflate(inflater, parent, false)
        return ChapterItemViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: ChapterItemViewHolder, position: Int) {
        val chapterItem = requireNotNull(getItem(position))
        holder.bindChapterItem(chapterItem)
        holder.setOnChapterClickListener { onChapterClickListener.invoke(chapterItem.startOffset) }
    }
}
