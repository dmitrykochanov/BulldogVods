package com.dmko.bulldogvods.app.screens.vodplayback

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.databinding.FragmentVodPlaybackBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VodPlaybackFragment : Fragment(R.layout.fragment_vod_playback) {

    private val viewModel: VodPlaybackViewModel by viewModels()
    private val binding by viewBinding(FragmentVodPlaybackBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.playerLiveData.observe(viewLifecycleOwner) { player -> binding.playerView.player = player }
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        binding.playerView
            .findViewById<ImageButton>(com.google.android.exoplayer2.R.id.exo_settings)
            .setOnClickListener(null)
    }
}
