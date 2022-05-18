package com.dmko.bulldogvods.di.modules.features

import com.dmko.bulldogvods.features.chat.data.network.datasource.ApolloNetworkChatDataSource
import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatReplayConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatModule {

    @Binds
    @Singleton
    fun provideNetworkChatDataSource(apolloNetworkChatDataSource: ApolloNetworkChatDataSource): NetworkChatDataSource

    companion object {

        @Provides
        @Singleton
        fun provideChatReplayConfiguration(): ChatReplayConfig {
            return ChatReplayConfig()
        }
    }
}
