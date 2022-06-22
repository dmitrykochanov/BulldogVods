package com.dmko.bulldogvods.app.screens.chatpositionchooser

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.DialogFragmentChatPositionChooserBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatPositionChooserDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_chat_position_chooser) {

    private val viewModel: ChatPositionChooserViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentChatPositionChooserBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.leftContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.LEFT)
        }
        binding.rightContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.RIGHT)
        }
        binding.leftOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.LEFT_OVERLAY)
        }
        binding.rightOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.RIGHT_OVERLAY)
        }
        binding.topLeftOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.TOP_LEFT_OVERLAY)
        }
        binding.topRightOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.TOP_RIGHT_OVERLAY)
        }
        binding.bottomLeftOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY)
        }
        binding.bottomRightOverlayContainer.setOnClickListener {
            viewModel.onLandscapePositionSelected(ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY)
        }
        binding.topContainer.setOnClickListener {
            viewModel.onPortraitPositionSelected(ChatPosition.Portrait.TOP)
        }
        binding.bottomContainer.setOnClickListener {
            viewModel.onPortraitPositionSelected(ChatPosition.Portrait.BOTTOM)
        }

        viewModel.landscapePositionLiveData.observe(viewLifecycleOwner, ::showLandscapePosition)
        viewModel.portraitPositionLiveData.observe(viewLifecycleOwner, ::showPortraitPosition)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        setLandscapePositionsVisible(isLandscape)
        setPortraitPositionsVisible(!isLandscape)
    }

    private fun showLandscapePosition(position: ChatPosition.Landscape) {
        binding.imageCheckLeft.isInvisible = position != ChatPosition.Landscape.LEFT
        binding.imageCheckRight.isInvisible = position != ChatPosition.Landscape.RIGHT
        binding.imageCheckLeftOverlay.isInvisible = position != ChatPosition.Landscape.LEFT_OVERLAY
        binding.imageCheckRightOverlay.isInvisible = position != ChatPosition.Landscape.RIGHT_OVERLAY
        binding.imageCheckTopLeftOverlay.isInvisible = position != ChatPosition.Landscape.TOP_LEFT_OVERLAY
        binding.imageCheckTopRightOverlay.isInvisible = position != ChatPosition.Landscape.TOP_RIGHT_OVERLAY
        binding.imageCheckBottomLeftOverlay.isInvisible = position != ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY
        binding.imageCheckBottomRightOverlay.isInvisible = position != ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY
    }

    private fun showPortraitPosition(position: ChatPosition.Portrait) {
        binding.imageCheckTop.isInvisible = position != ChatPosition.Portrait.TOP
        binding.imageCheckBottom.isInvisible = position != ChatPosition.Portrait.BOTTOM
    }

    private fun setLandscapePositionsVisible(isVisible: Boolean) {
        binding.leftContainer.isVisible = isVisible
        binding.rightContainer.isVisible = isVisible
        binding.leftOverlayContainer.isVisible = isVisible
        binding.rightOverlayContainer.isVisible = isVisible
        binding.topLeftOverlayContainer.isVisible = isVisible
        binding.topRightOverlayContainer.isVisible = isVisible
        binding.bottomLeftOverlayContainer.isVisible = isVisible
        binding.bottomRightOverlayContainer.isVisible = isVisible
    }

    private fun setPortraitPositionsVisible(isVisible: Boolean) {
        binding.topContainer.isVisible = isVisible
        binding.bottomContainer.isVisible = isVisible
    }
}
