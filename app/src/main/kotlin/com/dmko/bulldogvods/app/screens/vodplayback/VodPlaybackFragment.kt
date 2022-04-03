package com.dmko.bulldogvods.app.screens.vodplayback

import androidx.fragment.app.Fragment
import com.dmko.bulldogvods.R

class VodPlaybackFragment : Fragment(R.layout.fragment_vod_playback) {

    companion object {

        fun newInstance(vodId: String) = VodPlaybackFragment()
    }
}
