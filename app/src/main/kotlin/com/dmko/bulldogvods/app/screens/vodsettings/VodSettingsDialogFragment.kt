package com.dmko.bulldogvods.app.screens.vodsettings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.extensions.setOnStopTrackingTouchListener
import com.dmko.bulldogvods.databinding.DialogFragmentVodSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class VodSettingsDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_vod_settings) {

    private val viewModel: VodSettingsViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentVodSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.videoSourceChooserContainer.setOnClickListener { viewModel.onVideoSourceClicked() }
        binding.chatPositionChooserContainer.setOnClickListener { viewModel.onChatPositionClicked() }
        binding.chatTextSizeChooserContainer.setOnClickListener { viewModel.onChatTextSizeClicked() }
        binding.chatSizeSlider.setOnStopTrackingTouchListener(viewModel::onChatWidthSelected)
        binding.chatSizeSlider.setLabelFormatter(::formatChatWidthSliderValue)

        viewModel.selectedVideoSourceNameLiveData.observe(
            viewLifecycleOwner,
            binding.selectedVideoSourceText::setText
        )
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        binding.chatWidthChooserContainer.isVisible = isLandscape
        if (isLandscape) {
            viewModel.landscapeChatPositionLiveData.observe(
                viewLifecycleOwner,
                binding.selectedChatPositionText::setText
            )
        } else {
            viewModel.portraitChatPositionLiveData.observe(
                viewLifecycleOwner,
                binding.selectedChatPositionText::setText
            )
        }
        viewModel.selectedChatTextSizeLiveData.observe(viewLifecycleOwner, binding.selectedChatTextSizeText::setText)
        viewModel.selectedChatWidthLiveData.observe(viewLifecycleOwner, binding.chatSizeSlider::setValue)
    }

    private fun formatChatWidthSliderValue(value: Float): String {
        return requireContext().getString(R.string.dialog_vod_settings_chat_width_value_percentage, value.roundToInt())
    }
}
