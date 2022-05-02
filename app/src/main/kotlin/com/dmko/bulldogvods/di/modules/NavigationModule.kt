package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.app.navigation.NavigationCommandSource
import com.dmko.bulldogvods.app.navigation.NavigationCoordinator
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
    fun provideNavigationCommandSource(navigationCoordinator: NavigationCoordinator): NavigationCommandSource

    @Binds
    @Singleton
    fun provideNavigationCommandDispatcher(navigationCoordinator: NavigationCoordinator): NavigationCommandDispatcher

    companion object {

        @Provides
        @Singleton
        fun provideNavigationCoordinator() = NavigationCoordinator()
    }
}
