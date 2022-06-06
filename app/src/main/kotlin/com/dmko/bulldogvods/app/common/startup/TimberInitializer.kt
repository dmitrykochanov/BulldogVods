package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.dmko.bulldogvods.BuildConfig
import com.dmko.bulldogvods.app.common.logging.SentryTimberTree
import timber.log.Timber

class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        Timber.plant(SentryTimberTree())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
