package com.dmko.bulldogvods.app.screens.vod

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.screens.vodchat.VodChatFragment
import com.dmko.bulldogvods.app.screens.vodplayback.VodPlaybackFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class VodFragment : Fragment(R.layout.fragment_vod) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vodId = requireNotNull(requireArguments().getString(ARG_VOD_ID))
        val startOffset = requireArguments().getLong(ARG_START_OFFSET_MILLIS).milliseconds
        var transaction = childFragmentManager.beginTransaction()
        if (childFragmentManager.findFragmentById(R.id.vodPlaybackContainer) == null) {
            val vodPlaybackFragment = VodPlaybackFragment.newInstance(vodId, startOffset)
            transaction = transaction.add(R.id.vodPlaybackContainer, vodPlaybackFragment)
        }
        if (childFragmentManager.findFragmentById(R.id.vodChatContainer) == null) {
            val vodChatFragment = VodChatFragment.newInstance(vodId)
            transaction = transaction.add(R.id.vodChatContainer, vodChatFragment)
        }
        transaction.commit()
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
        private const val ARG_START_OFFSET_MILLIS = "start_offset_millis"
    }
}
