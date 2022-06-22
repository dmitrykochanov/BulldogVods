package com.dmko.bulldogvods.app.screens.vod

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.extensions.requireAppActivity
import com.dmko.bulldogvods.app.common.extensions.setOnDoubleClickListener
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.databinding.FragmentVodBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem
import com.dmko.bulldogvods.features.chat.presentation.recycler.messages.ChatMessageItemsAdapter
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class VodFragment : Fragment(R.layout.fragment_vod) {

    private val viewModel: VodViewModel by viewModels()
    private val binding by viewBinding(FragmentVodBinding::bind)

    private lateinit var chatMessageItemsAdapter: ChatMessageItemsAdapter

    private val scrollChatToBottomLayoutListener = OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        scrollChatToBottom()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        chatMessageItemsAdapter = ChatMessageItemsAdapter()
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
        viewModel.chatPositionLiveData.observe(viewLifecycleOwner, ::setChatPosition)
        viewModel.keepScreenOnLiveData.observe(viewLifecycleOwner, binding.playerView::setKeepScreenOn)
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
            .findViewById<ImageButton>(com.google.android.exoplayer2.ui.R.id.exo_settings)
            .setOnClickListener { viewModel.onVodSettingsClicked() }
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
            binding.playerView.setOnDoubleClickListener { viewModel.onLandscapePlayerDoubleClicked() }
        } else {
            binding.playerView.setOnDoubleClickListener { viewModel.onPortraitPlayerDoubleClicked() }
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
        stopSyncChatHeightWithPlayerControls()
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

    private fun setChatPosition(chatPosition: ChatPosition) {
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (isLandscape) {
            setLandscapeChatPosition(chatPosition.landscapePosition, chatPosition.isVisibleInLandscape)
        } else {
            setPortraitChatPosition(chatPosition.portraitPosition, chatPosition.isVisibleInPortrait)
        }
    }

    private fun setLandscapeChatPosition(position: ChatPosition.Landscape, isVisible: Boolean) {
        if (isVisible) {
            binding.chatContainer.isVisible = true
            when (position) {
                ChatPosition.Landscape.LEFT -> {
                    setLandscapePlayerRight()
                    setLandscapeChatLeft()
                    removeChatBackground()
                    stopSyncChatHeightWithPlayerControls()
                }
                ChatPosition.Landscape.RIGHT -> {
                    setLandscapePlayerLeft()
                    setLandscapeChatRight()
                    removeChatBackground()
                    stopSyncChatHeightWithPlayerControls()
                }
                ChatPosition.Landscape.LEFT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatLeft()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                }
                ChatPosition.Landscape.RIGHT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatRight()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                }
                ChatPosition.Landscape.TOP_LEFT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatLeft()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                    binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToBottom = R.id.middleChatGuideline
                    }
                }
                ChatPosition.Landscape.TOP_RIGHT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatRight()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                    binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToBottom = R.id.middleChatGuideline
                    }
                }
                ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatLeft()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                    binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToTop = R.id.middleChatGuideline
                    }
                }
                ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY -> {
                    setLandscapePlayerFullscreen()
                    setLandscapeChatRight()
                    setOverlayChatBackground()
                    startSyncChatHeightWithPlayerControls()
                    binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        topToTop = R.id.middleChatGuideline
                    }
                }
            }
        } else {
            binding.chatContainer.isVisible = false
        }
    }

    private fun setOverlayChatBackground() {
        val overlayChatColor = ContextCompat.getColor(requireContext(), R.color.black_50)
        binding.chatContainer.setBackgroundColor(overlayChatColor)
    }

    private fun removeChatBackground() {
        binding.chatContainer.setBackgroundColor(0)
    }

    private fun setLandscapePlayerFullscreen() {
        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun setLandscapePlayerLeft() {
        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = R.id.rightChatGuideline
        }
    }

    private fun setLandscapePlayerRight() {
        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = R.id.leftChatGuideline
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun setLandscapeChatLeft() {
        binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = R.id.leftChatGuideline
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun setLandscapeChatRight() {
        binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = R.id.rightChatGuideline
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun startSyncChatHeightWithPlayerControls() {
        binding.playerView.setControllerVisibilityListener(
            StyledPlayerView.ControllerVisibilityListener { visibility ->
                binding.chatContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = if (visibility == View.VISIBLE) {
                        binding.playerView
                            .findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_bottom_bar)
                            .height
                    } else {
                        0
                    }
                    topMargin = if (visibility == View.VISIBLE) {
                        binding.playerView
                            .findViewById<View>(R.id.topBarContainer)
                            .height
                    } else {
                        0
                    }
                }
            }
        )
    }

    private fun stopSyncChatHeightWithPlayerControls() {
        binding.playerView.setControllerVisibilityListener(null as StyledPlayerView.ControllerVisibilityListener?)
        binding.chatContainer.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = 0
            topMargin = 0
        }
    }

    private fun setPortraitChatPosition(position: ChatPosition.Portrait, isVisible: Boolean) {
        if (isVisible) {
            binding.chatContainer.isVisible = true
            when (position) {
                ChatPosition.Portrait.TOP -> {
                    setPortraitChatTop()
                    setPortraitPlayerBottom()
                }
                ChatPosition.Portrait.BOTTOM -> {
                    setPortraitPlayerTop()
                    setPortraitChatBottom()
                }
            }
        } else {
            binding.chatContainer.isVisible = false
            binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }
    }

    private fun setPortraitPlayerTop() {
        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        }
    }

    private fun setPortraitPlayerBottom() {
        binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToTop = R.id.playerView
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topToBottom = ConstraintLayout.LayoutParams.UNSET
        }
    }

    private fun setPortraitChatTop() {
        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            topToTop = ConstraintLayout.LayoutParams.UNSET
        }
    }

    private fun setPortraitChatBottom() {
        binding.chatContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            topToBottom = R.id.playerView
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            topToTop = ConstraintLayout.LayoutParams.UNSET
        }
    }
}
