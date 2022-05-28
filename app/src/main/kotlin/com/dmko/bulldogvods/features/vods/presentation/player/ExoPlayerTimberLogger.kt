package com.dmko.bulldogvods.features.vods.presentation.player

import com.google.android.exoplayer2.util.EventLogger
import timber.log.Timber

class ExoPlayerTimberLogger : EventLogger(null) {

    override fun logd(message: String) {
        Timber.d(message)
    }

    override fun loge(message: String) {
        Timber.e(message)
    }
}
