package com.dmko.bulldogvods.app.screens.vod

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.extensions.enterFullscreen
import com.dmko.bulldogvods.app.common.extensions.exitFullscreen
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.FragmentVodBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.presentation.recycler.messages.ChatMessagesAdapter
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VodFragment : Fragment(R.layout.fragment_vod) {

    private val viewModel: VodViewModel by viewModels()
    private val binding by viewBinding(FragmentVodBinding::bind)

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var chatMessagesAdapter: ChatMessagesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        chatMessagesAdapter = ChatMessagesAdapter(imageLoader)
        binding.recyclerChat.adapter = chatMessagesAdapter
        binding.recyclerChat.itemAnimator = null

        viewModel.playerLiveData.observe(viewLifecycleOwner, ::showPlayerResource)
        viewModel.chatMessagesLiveData.observe(viewLifecycleOwner, ::showChatMessages)

        binding.layoutError.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        binding.playerView
            .findViewById<ImageButton>(R.id.exo_chapters)
            .setOnClickListener { viewModel.onVodChaptersClicked() }
        binding.playerView
            .findViewById<ImageButton>(com.google.android.exoplayer2.R.id.exo_settings)
            .setOnClickListener { viewModel.onVodPlaybackSettingsClicked() }
    }

    override fun onStart() {
        super.onStart()
        enterFullscreenIfLandscape()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().exitFullscreen()
    }

    private fun enterFullscreenIfLandscape() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) {
            requireActivity().enterFullscreen()
        } else {
            requireActivity().exitFullscreen()
        }
    }

    private fun showPlayerResource(playerResource: Resource<Player>) {
        when (playerResource) {
            is Resource.Loading -> {
                binding.playerProgressBar.isVisible = true
                binding.playerView.isVisible = false
                binding.layoutError.root.isVisible = false
                binding.playerView.player = null
            }
            is Resource.Data -> {
                binding.playerProgressBar.isVisible = false
                binding.playerView.isVisible = true
                binding.layoutError.root.isVisible = false
                binding.playerView.player = playerResource.data
            }
            is Resource.Error -> {
                binding.playerProgressBar.isVisible = false
                binding.playerView.isVisible = false
                binding.layoutError.root.isVisible = true
                binding.playerView.player = null
            }
        }
    }

    private fun showChatMessages(chatMessages: Resource<List<ChatMessage>>) {
        if (chatMessages is Resource.Data) {
            chatMessagesAdapter.submitList(chatMessages.data)
            binding.recyclerChat.smoothScrollToPosition(chatMessagesAdapter.itemCount)
        }
    }
}
