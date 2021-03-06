package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import androidx.startup.Initializer
import com.dmko.bulldogvods.di.entrypoints.ThemeInitializerEntryPoint
import dagger.hilt.android.EntryPointAccessors

class ThemeInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication<ThemeInitializerEntryPoint>(context)
        entryPoint.initializeThemeUseCase.execute().blockingAwait()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
