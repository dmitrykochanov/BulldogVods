package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.common.device.orientation.AndroidDeviceOrientationProvider
import com.dmko.bulldogvods.app.common.device.orientation.DeviceOrientationProvider
import com.dmko.bulldogvods.app.common.image.loader.CoilImageLoader
import com.dmko.bulldogvods.app.common.image.loader.CoilImageLoaderFactory
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.common.schedulers.SchedulersImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {

    @Binds
    @Singleton
    fun provideImageLoader(coilImageLoader: CoilImageLoader): ImageLoader

    @Binds
    @Singleton
    fun provideSchedulers(schedulersImpl: SchedulersImpl): Schedulers

    @Binds
    @Singleton
    fun provideDeviceOrientationProvider(
        androidDeviceOrientationProvider: AndroidDeviceOrientationProvider
    ): DeviceOrientationProvider

    companion object {

        @Provides
        @Singleton
        fun provideEventBus(): EventBus {
            return EventBus.getDefault()
        }

        @Provides
        @Singleton
        fun provideCoilImageLoader(coilImageLoaderFactory: CoilImageLoaderFactory): coil.ImageLoader {
            return coilImageLoaderFactory.createCoilImageLoader()
        }
    }
}
