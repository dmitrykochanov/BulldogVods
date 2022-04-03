package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemVodBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection.*

class VodItemViewHolder(
    private val binding: ListItemVodBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindVodItem(vodItem: VodItem) {
        imageLoader.load(vodItem.thumbnailUrl, binding.imageVodThumbnail)
        binding.textLength.text = vodItem.length
        binding.textLength.isVisible = vodItem.length != null
        binding.textRecordedAt.text = vodItem.recordedAt
        bindStateBadge(vodItem.stateBadge)

        imageLoader.load(vodItem.gameThumbnailUrl, binding.imageGameThumbnail)
        binding.textTitle.text = vodItem.title
        bindChaptersSection(vodItem.chaptersSection)
    }

    private fun bindStateBadge(stateBadge: VodItem.StateBadge?) {
        if (stateBadge != null) {
            binding.textState.isVisible = true
            binding.textState.setText(stateBadge.text)
            DrawableCompat.setTint(
                DrawableCompat.wrap(binding.textState.background.mutate()),
                ContextCompat.getColor(binding.root.context, stateBadge.backgroundColor)
            )
        } else {
            binding.textState.isVisible = false
        }
    }

    private fun bindChaptersSection(chaptersSection: ChaptersSection) {
        when (chaptersSection) {
            is NoChapters -> {
                binding.cardChapters.isVisible = false
                binding.textSingleChapterGameName.isVisible = false
            }
            is SingleChapter -> {
                binding.cardChapters.isVisible = false
                binding.textSingleChapterGameName.isVisible = true
                binding.textSingleChapterGameName.text = chaptersSection.gameName
            }
            is MultipleChapters -> {
                binding.cardChapters.isVisible = true
                binding.textSingleChapterGameName.isVisible = false
                binding.textChaptersCount.text = chaptersSection.chaptersCount.toString()
            }
        }
    }

    fun setOnVodClickListener(onVodClickListener: () -> Unit) {
        binding.root.setOnClickListener { onVodClickListener.invoke() }
    }

    fun setOnVodChaptersClickListener(onVodChaptersClickListener: () -> Unit) {
        binding.cardChapters.setOnClickListener { onVodChaptersClickListener.invoke() }
    }
}
