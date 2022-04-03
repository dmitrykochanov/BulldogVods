package com.dmko.bulldogvods.di.modules

import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.common.schedulers.SchedulersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SchedulersModule {

    @Binds
    @Singleton
    fun provideSchedulers(schedulersImpl: SchedulersImpl): Schedulers
}
