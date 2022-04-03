package com.dmko.bulldogvods.features.vods.presentation.recycler.chapters

import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChapterBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem

class ChapterItemViewHolder(
    private val binding: ListItemChapterBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindChapterItem(chapterItem: ChapterItem) {
        imageLoader.load(chapterItem.gameThumbnailUrl, binding.imageGameThumbnail)
        binding.textGameName.text = chapterItem.gameName
        binding.textLength.text = chapterItem.length
    }

    fun setOnChapterClickListener(onChapterClickListener: () -> Unit) {
        binding.root.setOnClickListener { onChapterClickListener.invoke() }
    }
}
