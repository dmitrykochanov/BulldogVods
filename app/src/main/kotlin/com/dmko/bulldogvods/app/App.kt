package com.dmko.bulldogvods.app

import android.app.Application
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
