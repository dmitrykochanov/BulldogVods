package com.dmko.bulldogvods.app.screens.chapterchooser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.DialogFragmentChapterChooserBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem
import com.dmko.bulldogvods.features.vods.presentation.recycler.chapters.ChapterItemsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChapterChooserDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_chapter_chooser) {

    private val viewModel: ChapterChooserViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentChapterChooserBinding::bind)

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var chapterItemsAdapter: ChapterItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.layoutError.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        chapterItemsAdapter = ChapterItemsAdapter(
            imageLoader = imageLoader,
            onChapterClickListener = viewModel::onChapterClicked
        )
        binding.recyclerChapterItems.adapter = chapterItemsAdapter

        viewModel.chapterItemsLiveData.observe(viewLifecycleOwner, ::showChapterItems)
    }

    private fun showChapterItems(chapterItems: Resource<List<ChapterItem>>) {
        when (chapterItems) {
            is Resource.Loading -> {
                binding.progressBar.isVisible = true
                binding.layoutError.root.isVisible = false
            }
            is Resource.Data -> {
                binding.progressBar.isVisible = false
                binding.layoutError.root.isVisible = false
                chapterItemsAdapter.submitList(chapterItems.data)
            }
            is Resource.Error -> {
                binding.progressBar.isVisible = false
                binding.layoutError.root.isVisible = true
                Timber.e(chapterItems.error, "Failed to load vod chapters")
            }
        }
    }
}
