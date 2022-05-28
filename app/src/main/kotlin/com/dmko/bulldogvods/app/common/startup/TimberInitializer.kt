package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import com.dmko.bulldogvods.BuildConfig
import timber.log.Timber

class TimberInitializer : IndependentInitializer() {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
