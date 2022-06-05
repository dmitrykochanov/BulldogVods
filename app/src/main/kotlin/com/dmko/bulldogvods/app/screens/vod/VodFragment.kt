package com.dmko.bulldogvods.app.screens.vod

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.extensions.requireAppActivity
import com.dmko.bulldogvods.app.common.extensions.setOnDoubleClickListener
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.FragmentVodBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem
import com.dmko.bulldogvods.features.chat.presentation.recycler.messages.ChatMessageItemsAdapter
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class VodFragment : Fragment(R.layout.fragment_vod) {

    private val viewModel: VodViewModel by viewModels()
    private val binding by viewBinding(FragmentVodBinding::bind)

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var chatMessageItemsAdapter: ChatMessageItemsAdapter

    private val scrollChatToBottomLayoutListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        scrollChatToBottom()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        chatMessageItemsAdapter = ChatMessageItemsAdapter(imageLoader)
        binding.recyclerChat.adapter = chatMessageItemsAdapter
        binding.recyclerChat.itemAnimator = null
        setupChatAutoScroll()
        setupChatVisibilityToggle()

        viewModel.titleLiveData.observe(viewLifecycleOwner, ::showTitle)
        viewModel.playerLiveData.observe(viewLifecycleOwner) { playerResource ->
            onPlayerOrChatChanged(playerResource, viewModel.chatMessageItemsLiveData.value)
        }
        viewModel.chatMessageItemsLiveData.observe(viewLifecycleOwner) { chatMessages ->
            onPlayerOrChatChanged(viewModel.playerLiveData.value, chatMessages)
        }
        viewModel.isAutoScrollPausedLiveData.observe(viewLifecycleOwner, ::onAutoScrollStateChanged)

        binding.layoutError.buttonRetry.setOnClickListener { viewModel.onRetryClicked() }
        binding.layoutChatError.buttonRetry.setOnClickListener { viewModel.onRetryChatClicked() }

        binding.playerView
            .findViewById<ImageView>(R.id.backButton)
            .setOnClickListener { viewModel.onBackClicked() }
        binding.playerView
            .findViewById<ImageButton>(R.id.exo_chapters)
            .setOnClickListener { viewModel.onVodChaptersClicked() }
        binding.playerView
            .findViewById<ImageButton>(com.google.android.exoplayer2.R.id.exo_settings)
            .setOnClickListener { viewModel.onVodPlaybackSettingsClicked() }
    }

    private fun setupChatAutoScroll() {
        binding.buttonScrollToBottom.setOnClickListener {
            viewModel.onAutoScrollResumed()
        }
        binding.recyclerChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val shouldPauseAutoScroll = when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING, RecyclerView.SCROLL_STATE_SETTLING -> true
                    RecyclerView.SCROLL_STATE_IDLE -> recyclerView.canScrollVertically(1)
                    else -> false
                }
                if (shouldPauseAutoScroll) {
                    viewModel.onAutoScrollPaused()
                } else {
                    viewModel.onAutoScrollResumed()
                }
            }
        })
    }

    private fun setupChatVisibilityToggle() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) {
            binding.playerView.setOnDoubleClickListener {
                binding.chatContainer.isVisible = !binding.chatContainer.isVisible
            }
        }
    }

    override fun onStart() {
        super.onStart()
        enterFullscreenIfLandscape()
    }

    override fun onStop() {
        super.onStop()
        requireAppActivity().exitFullscreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
    }

    private fun enterFullscreenIfLandscape() {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) {
            requireAppActivity().enterFullscreen()
        } else {
            requireAppActivity().exitFullscreen()
        }
    }

    private fun showTitle(title: Resource<String>) {
        val topControlsContainer = binding.playerView.findViewById<View>(R.id.topBarContainer)
        when (title) {
            is Resource.Loading -> {
                topControlsContainer.isVisible = false
            }
            is Resource.Data -> {
                topControlsContainer.isVisible = true
                binding.playerView.findViewById<TextView>(R.id.titleTextView).text = title.data
            }
            is Resource.Error -> {
                topControlsContainer.isVisible = false
                Timber.e(title.error, "Failed to load vod title")
            }
        }
    }

    private fun onPlayerOrChatChanged(
        playerResource: Resource<Player>?,
        chatMessageItemsResource: Resource<List<ChatMessageItem>>?
    ) {
        if (playerResource != null && chatMessageItemsResource != null) {
            showPlayerResource(playerResource)
            if (playerResource is Resource.Data) {
                showChatMessagesResource(chatMessageItemsResource)
            } else {
                hideChat()
            }
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
                Timber.e(playerResource.error, "Failed to load vod player")
            }
        }
    }

    private fun showChatMessagesResource(chatMessageItemsResource: Resource<List<ChatMessageItem>>) {
        when (chatMessageItemsResource) {
            is Resource.Loading -> {
                binding.recyclerChat.isVisible = false
                binding.chatProgressBar.isVisible = true
                binding.layoutChatError.root.isVisible = false
            }
            is Resource.Data -> {
                binding.recyclerChat.isVisible = true
                binding.chatProgressBar.isVisible = false
                binding.layoutChatError.root.isVisible = false
                chatMessageItemsAdapter.submitList(chatMessageItemsResource.data)
            }
            is Resource.Error -> {
                binding.recyclerChat.isVisible = false
                binding.chatProgressBar.isVisible = false
                binding.layoutChatError.root.isVisible = true
                Timber.e(chatMessageItemsResource.error, "Failed to load chat messages")
            }
        }
    }

    private fun hideChat() {
        binding.recyclerChat.isVisible = false
        binding.chatProgressBar.isVisible = false
        binding.layoutChatError.root.isVisible = false
    }

    private fun onAutoScrollStateChanged(isAutoScrollPaused: Boolean) {
        binding.buttonScrollToBottom.isVisible = isAutoScrollPaused
        if (isAutoScrollPaused) {
            binding.recyclerChat.removeOnLayoutChangeListener(scrollChatToBottomLayoutListener)
        } else {
            scrollChatToBottom()
            binding.recyclerChat.addOnLayoutChangeListener(scrollChatToBottomLayoutListener)
        }
    }

    private fun scrollChatToBottom() {
        binding.recyclerChat.scrollBy(0, Int.MAX_VALUE)
    }
}
