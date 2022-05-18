package com.dmko.bulldogvods.di.modules.features.vods

import com.dmko.bulldogvods.features.vods.data.network.datasource.ApolloNetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface VodsModule {

    @Binds
    @Singleton
    fun provideNetworkVodsDataSource(
        apolloNetworkVodsDataSource: ApolloNetworkVodsDataSource
    ): NetworkVodsDataSource
}
