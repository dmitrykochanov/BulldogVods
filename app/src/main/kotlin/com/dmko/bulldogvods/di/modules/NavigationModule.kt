package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.app.navigation.NavigationEventSource
import com.dmko.bulldogvods.app.navigation.ScreensNavigator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {

    @Binds
    @Singleton
    fun provideNavigationEventSource(screensNavigator: ScreensNavigator): NavigationEventSource

    @Binds
    @Singleton
    fun provideNavigationEventDispatcher(
        screensNavigator: ScreensNavigator
    ): NavigationEventDispatcher

    companion object {

        @Provides
        @Singleton
        fun provideScreensNavigator() = ScreensNavigator()
    }
}
