package com.dmko.bulldogvods.features.chat.data.network.datasource

import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.GetChatMessagesRequest
import io.reactivex.rxjava3.core.Single

interface NetworkChatDataSource {

    fun getChatMessages(request: GetChatMessagesRequest): Single<List<ChatMessage>>
}
