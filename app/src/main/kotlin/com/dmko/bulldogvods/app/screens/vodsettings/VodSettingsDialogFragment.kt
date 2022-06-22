package com.dmko.bulldogvods.app.screens.vodsettings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.DialogFragmentVodSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VodSettingsDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_vod_settings) {

    private val viewModel: VodSettingsViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentVodSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.videoSourceChooserContainer.setOnClickListener { viewModel.onVideoSourceClicked() }
        binding.chatPositionChooserContainer.setOnClickListener { viewModel.onChatPositionClicked() }

        viewModel.selectedVideoSourceNameLiveData.observe(
            viewLifecycleOwner,
            binding.selectedVideoSourceText::setText
        )
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
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
    }
}
