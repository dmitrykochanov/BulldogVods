package com.dmko.bulldogvods.features.vods.presentation.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoPlayerFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createExoPlayer(): ExoPlayer {
        val exoPlayer = ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(SEEK_INCREMENT_MILLIS)
            .setSeekForwardIncrementMs(SEEK_INCREMENT_MILLIS)
            .build()
        exoPlayer.prepare()
        exoPlayer.addAnalyticsListener(ExoPlayerTimberLogger())
        return exoPlayer
    }

    private companion object {

        private const val SEEK_INCREMENT_MILLIS = 10_000L
    }
}
