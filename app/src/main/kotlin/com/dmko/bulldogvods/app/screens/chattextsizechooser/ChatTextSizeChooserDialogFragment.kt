package com.dmko.bulldogvods.app.screens.chattextsizechooser

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.DialogFragmentChatTextSizeChooserBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatTextSizeChooserDialogFragment : AppCompatDialogFragment(R.layout.dialog_fragment_chat_text_size_chooser) {

    private val viewModel: ChatTextSizeChooserViewModel by viewModels()
    private val binding by viewBinding(DialogFragmentChatTextSizeChooserBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.smallContainer.setOnClickListener { viewModel.onTextSizeSelected(ChatTextSize.SMALL) }
        binding.normalContainer.setOnClickListener { viewModel.onTextSizeSelected(ChatTextSize.NORMAL) }
        binding.largeContainer.setOnClickListener { viewModel.onTextSizeSelected(ChatTextSize.LARGE) }
        binding.hugeContainer.setOnClickListener { viewModel.onTextSizeSelected(ChatTextSize.HUGE) }

        viewModel.chatTextSizeLiveData.observe(viewLifecycleOwner, ::showSelectedTextSize)
    }

    private fun showSelectedTextSize(size: ChatTextSize) {
        binding.imageCheckSmall.isInvisible = size != ChatTextSize.SMALL
        binding.imageCheckNormal.isInvisible = size != ChatTextSize.NORMAL
        binding.imageCheckLarge.isInvisible = size != ChatTextSize.LARGE
        binding.imageCheckHuge.isInvisible = size != ChatTextSize.HUGE
    }
}
