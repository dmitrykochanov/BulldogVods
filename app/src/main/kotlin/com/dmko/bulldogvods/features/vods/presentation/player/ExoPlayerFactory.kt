package com.dmko.bulldogvods.features.vods.presentation.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ExoPlayerFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createExoPlayer(): ExoPlayer {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(CONNECT_TIMEOUT_MILLIS)
            .setReadTimeoutMs(READ_TIMEOUT_MILLIS)
        val exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory, DefaultExtractorsFactory()))
            .setSeekBackIncrementMs(SEEK_INCREMENT_MILLIS)
            .setSeekForwardIncrementMs(SEEK_INCREMENT_MILLIS)
            .build()
        exoPlayer.prepare()
        exoPlayer.addAnalyticsListener(ExoPlayerTimberLogger())
        return exoPlayer
    }

    private companion object {

        private const val SEEK_INCREMENT_MILLIS = 10_000L
        private const val CONNECT_TIMEOUT_MILLIS = 30_000
        private const val READ_TIMEOUT_MILLIS = 30_000
    }
}
