package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import com.dmko.bulldogvods.di.entrypoints.ThemeInitializerEntryPoint
import dagger.hilt.android.EntryPointAccessors

class ThemeInitializer : IndependentInitializer() {

    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication<ThemeInitializerEntryPoint>(context)
        entryPoint.initializeThemeUseCase.execute().blockingAwait()
    }
}
