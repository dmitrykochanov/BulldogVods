package com.dmko.bulldogvods.di.modules.features

import android.content.Context
import com.dmko.bulldogvods.features.theming.data.device.AndroidDeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.device.DeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.local.DataStoreLocalThemeDataSource
import com.dmko.bulldogvods.features.theming.data.local.LocalThemeDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ThemingModule {

    @Binds
    @Singleton
    fun provideDeviceThemeManager(androidDeviceThemeManager: AndroidDeviceThemeManager): DeviceThemeManager

    companion object {

        @Provides
        @Singleton
        @ExperimentalCoroutinesApi
        fun provide(@ApplicationContext context: Context): LocalThemeDataSource {
            return DataStoreLocalThemeDataSource(context)
        }
    }
}
