package com.dmko.bulldogvods.di.modules.features

import com.dmko.bulldogvods.features.theming.data.device.AndroidDeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.device.DeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.local.DataStoreLocalThemeDataSource
import com.dmko.bulldogvods.features.theming.data.local.LocalThemeDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ThemingModule {

    @Binds
    @Singleton
    fun provideDeviceThemeManager(androidDeviceThemeManager: AndroidDeviceThemeManager): DeviceThemeManager

    @Binds
    @Singleton
    @ExperimentalCoroutinesApi
    fun provideLocalThemeDataSource(dataStoreLocalThemeDataSource: DataStoreLocalThemeDataSource): LocalThemeDataSource
}
