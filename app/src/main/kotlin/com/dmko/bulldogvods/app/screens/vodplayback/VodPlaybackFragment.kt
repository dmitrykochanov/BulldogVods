package com.dmko.bulldogvods.app.screens.vodplayback

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.FragmentVodPlaybackBinding
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VodPlaybackFragment : Fragment(R.layout.fragment_vod_playback) {

    private val viewModel: VodPlaybackViewModel by viewModels()
    private val binding by viewBinding(FragmentVodPlaybackBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        viewModel.playerLiveData.observe(viewLifecycleOwner, ::showPlayerResource)

        binding.layoutError.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        binding.playerView
            .findViewById<ImageButton>(R.id.exo_chapters)
            .setOnClickListener { viewModel.onVodChaptersClicked() }
        binding.playerView
            .findViewById<ImageButton>(com.google.android.exoplayer2.R.id.exo_settings)
            .setOnClickListener { viewModel.onVodPlaybackSettingsClicked() }
    }

    private fun showPlayerResource(playerResource: Resource<Player>) {
        when (playerResource) {
            is Resource.Loading -> {
                binding.progressBar.isVisible = true
                binding.playerView.isVisible = false
                binding.layoutError.root.isVisible = false
                binding.playerView.player = null
            }
            is Resource.Data -> {
                binding.progressBar.isVisible = false
                binding.playerView.isVisible = true
                binding.layoutError.root.isVisible = false
                binding.playerView.player = playerResource.data
            }
            is Resource.Error -> {
                binding.progressBar.isVisible = false
                binding.playerView.isVisible = false
                binding.layoutError.root.isVisible = true
                binding.playerView.player = null
            }
        }
    }
}
