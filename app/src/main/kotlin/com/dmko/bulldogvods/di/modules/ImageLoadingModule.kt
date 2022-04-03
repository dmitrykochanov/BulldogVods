package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.common.imageloader.CoilImageLoader
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ImageLoadingModule {

    @Binds
    @Singleton
    fun provideImageLoader(coilImageLoader: CoilImageLoader): ImageLoader
}
