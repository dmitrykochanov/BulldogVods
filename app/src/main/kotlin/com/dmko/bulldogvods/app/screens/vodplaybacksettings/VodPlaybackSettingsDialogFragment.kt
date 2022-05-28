package com.dmko.bulldogvods.app.screens.vodplaybacksettings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.DialogFragmentVodPlaybackSettingsBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem
import com.dmko.bulldogvods.features.vods.presentation.recycler.videosources.VideoSourceItemsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VodPlaybackSettingsDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_vod_playback_settings) {

    private val viewModel: VodPlaybackSettingsViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentVodPlaybackSettingsBinding::bind)

    private lateinit var videoSourceItemsAdapter: VideoSourceItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.layoutError.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        videoSourceItemsAdapter = VideoSourceItemsAdapter(
            onVideoSourceClickListener = viewModel::onVideoSourceClicked
        )
        binding.recyclerVideoSourceItems.adapter = videoSourceItemsAdapter

        viewModel.videoSourceItemsLiveData.observe(viewLifecycleOwner, ::showVideoSourceItems)
    }

    private fun showVideoSourceItems(videoSourceItems: Resource<List<VideoSourceItem>>) {
        when (videoSourceItems) {
            is Resource.Loading -> {
                binding.progressBar.isVisible = true
                binding.layoutError.root.isVisible = false
            }
            is Resource.Data -> {
                binding.progressBar.isVisible = false
                binding.layoutError.root.isVisible = false
                videoSourceItemsAdapter.submitList(videoSourceItems.data)
            }
            is Resource.Error -> {
                binding.progressBar.isVisible = false
                binding.layoutError.root.isVisible = true
                Timber.e(videoSourceItems.error, "Failed to load video sources")
            }
        }
    }
}
