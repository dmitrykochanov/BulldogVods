package com.dmko.bulldogvods.features.chat.data.network.datasource

import apollo.ChatMessagesQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.rx3.rxSingle
import com.dmko.bulldogvods.features.chat.data.network.mapping.MessageSchemaToChatMessageMapper
import com.dmko.bulldogvods.features.chat.data.network.mapping.TimestampToDateStringMapper
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.GetChatMessagesRequest
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ApolloNetworkChatDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val messageSchemaToChatMessageMapper: MessageSchemaToChatMessageMapper,
    private val timestampToDateStringMapper: TimestampToDateStringMapper
) : NetworkChatDataSource {

    override fun getChatMessages(request: GetChatMessagesRequest): Single<List<ChatMessage>> {
        val query = ChatMessagesQuery(
            vodId = request.vodId,
            limit = request.limit,
            before = timestampToDateStringMapper.map(request.beforeMillis),
            after = timestampToDateStringMapper.map(request.afterMillis)
        )
        return apolloClient.query(query)
            .rxSingle()
            .map { apolloResponse -> requireNotNull(apolloResponse.data?.messages) }
            .map { chatMessages -> chatMessages.map(messageSchemaToChatMessageMapper::map) }
    }
}
