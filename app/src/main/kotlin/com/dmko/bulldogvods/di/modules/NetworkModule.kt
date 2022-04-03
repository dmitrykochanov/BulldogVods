package com.dmko.bulldogvods.di.modules

import com.apollographql.apollo3.ApolloClient
import com.dmko.bulldogvods.app.common.network.ApolloClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(apolloClientFactory: ApolloClientFactory): ApolloClient {
        return apolloClientFactory.createApolloClient()
    }
}
