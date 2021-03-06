package com.dmko.bulldogvods.features.vods.presentation.recycler.vods

import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemVodBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection.MultipleChapters
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection.NoChapters
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem.ChaptersSection.SingleChapter

class VodItemViewHolder(
    private val binding: ListItemVodBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindVodItem(vodItem: VodItem) {
        imageLoader.load(vodItem.thumbnailUrl, binding.imageVodThumbnail)
        binding.textLength.text = vodItem.length
        binding.textLength.isVisible = vodItem.length != null
        binding.textRecordedAt.text = vodItem.recordedAt
        binding.progressView.progress = vodItem.playbackPercentage
        binding.progressView.isVisible = vodItem.playbackPercentage != 0
        showStateBadge(vodItem.stateBadge)

        imageLoader.load(vodItem.gameThumbnailUrl, binding.imageGameThumbnail)
        binding.textTitle.text = vodItem.title
        showChaptersSection(vodItem.chaptersSection)
    }

    private fun showStateBadge(stateBadge: VodItem.StateBadge?) {
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

    private fun showChaptersSection(chaptersSection: ChaptersSection) {
        when (chaptersSection) {
            is NoChapters -> {
                binding.chaptersContainer.isVisible = false
                binding.textSingleChapterGameName.isVisible = false
            }
            is SingleChapter -> {
                binding.chaptersContainer.isVisible = false
                binding.textSingleChapterGameName.isVisible = true
                binding.textSingleChapterGameName.text = chaptersSection.gameName
            }
            is MultipleChapters -> {
                binding.chaptersContainer.isVisible = true
                binding.textSingleChapterGameName.isVisible = false
                binding.textChaptersCount.text = chaptersSection.chaptersCount.toString()
            }
        }
    }

    fun setOnVodClickListener(onVodClickListener: () -> Unit) {
        binding.root.setOnClickListener { onVodClickListener.invoke() }
    }

    fun setOnVodChaptersClickListener(onVodChaptersClickListener: () -> Unit) {
        binding.chaptersContainer.setOnClickListener { onVodChaptersClickListener.invoke() }
    }
}
