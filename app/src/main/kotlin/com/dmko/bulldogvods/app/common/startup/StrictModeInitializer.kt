package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import android.os.StrictMode
import androidx.startup.Initializer
import com.dmko.bulldogvods.BuildConfig

class StrictModeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
