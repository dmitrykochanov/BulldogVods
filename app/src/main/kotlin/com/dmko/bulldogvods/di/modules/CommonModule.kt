package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.common.imageloader.CoilImageLoader
import com.dmko.bulldogvods.app.common.imageloader.CoilImageLoaderFactory
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
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
