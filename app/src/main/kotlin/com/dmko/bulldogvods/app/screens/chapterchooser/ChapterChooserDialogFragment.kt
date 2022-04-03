package com.dmko.bulldogvods.app.screens.chapterchooser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.DialogFragmentChapterChooserBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItemsState
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItemsState.*
import com.dmko.bulldogvods.features.vods.presentation.recycler.chapters.ChapterItemsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChapterChooserDialog : AppCompatDialogFragment(R.layout.dialog_fragment_chapter_chooser) {

    private val viewModel: ChapterChooserViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentChapterChooserBinding::bind)

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var chapterItemsAdapter: ChapterItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageClose.setOnClickListener { dismiss() }
        binding.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        chapterItemsAdapter = ChapterItemsAdapter(
            imageLoader = imageLoader,
            onChapterClickListener = viewModel::onChapterClicked
        )
        binding.recyclerChapterItems.adapter = chapterItemsAdapter

        viewModel.chapterItemsStateLiveData.observe(viewLifecycleOwner, ::showChapterItemsState)
    }

    private fun showChapterItemsState(chapterItemsState: ChapterItemsState) {
        when (chapterItemsState) {
            is Loading -> {
                binding.progressBar.isVisible = true
                binding.layoutError.isVisible = false
            }
            is Data -> {
                binding.progressBar.isVisible = false
                binding.layoutError.isVisible = false
                chapterItemsAdapter.submitList(chapterItemsState.chapterItems)
            }
            is Error -> {
                binding.progressBar.isVisible = false
                binding.layoutError.isVisible = true
            }
        }
    }
}
