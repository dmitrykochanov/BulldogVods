package com.dmko.bulldogvods.di.entrypoints

import com.dmko.bulldogvods.features.theming.domain.usecases.InitializeThemeUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ThemeInitializerEntryPoint {

    val initializeThemeUseCase: InitializeThemeUseCase
}
