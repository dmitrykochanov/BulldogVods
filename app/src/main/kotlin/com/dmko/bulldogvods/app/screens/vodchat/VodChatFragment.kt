package com.dmko.bulldogvods.app.screens.vodchat

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.FragmentVodChatBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VodChatFragment : Fragment(R.layout.fragment_vod_chat) {

    private val viewModel: VodChatViewModel by viewModels()
    private val binding by viewBinding(FragmentVodChatBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel
    }

    companion object {

        fun newInstance(vodId: String): VodChatFragment {
            return VodChatFragment().apply {
                arguments = bundleOf(VodChatViewModel.ARG_VOD_ID to vodId)
            }
        }
    }
}
