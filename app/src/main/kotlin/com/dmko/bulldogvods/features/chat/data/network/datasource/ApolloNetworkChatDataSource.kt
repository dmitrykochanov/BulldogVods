package com.dmko.bulldogvods.features.chat.data.network.datasource

import apollo.ChatMessagesQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.doNotStore
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.rx3.rxSingle
import com.dmko.bulldogvods.features.chat.data.network.mapping.MessageSchemaToChatMessageMapper
import com.dmko.bulldogvods.features.chat.data.network.mapping.TimestampToDateStringMapper
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ApolloNetworkChatDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val messageSchemaToChatMessageMapper: MessageSchemaToChatMessageMapper,
    private val timestampToDateStringMapper: TimestampToDateStringMapper
) : NetworkChatDataSource {

    override fun getChatMessages(vodId: String, afterMillis: Long, beforeMillis: Long): Single<List<ChatMessage>> {
        val query = ChatMessagesQuery(
            vodId = vodId,
            after = timestampToDateStringMapper.map(afterMillis),
            before = timestampToDateStringMapper.map(beforeMillis)
        )
        return apolloClient.query(query)
            .doNotStore(true)
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .rxSingle()
            .map { apolloResponse -> requireNotNull(apolloResponse.data?.messages) }
            .map { chatMessages -> chatMessages.map(messageSchemaToChatMessageMapper::map) }
    }
}
