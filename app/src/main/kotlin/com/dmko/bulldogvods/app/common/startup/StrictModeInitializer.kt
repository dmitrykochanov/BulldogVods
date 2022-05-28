package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import android.os.StrictMode
import com.dmko.bulldogvods.BuildConfig

class StrictModeInitializer : IndependentInitializer() {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults()
        }
    }
}
