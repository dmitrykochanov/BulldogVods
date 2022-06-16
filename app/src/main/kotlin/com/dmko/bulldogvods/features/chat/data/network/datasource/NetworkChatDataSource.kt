package com.dmko.bulldogvods.features.chat.data.network.datasource

import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import io.reactivex.rxjava3.core.Single

interface NetworkChatDataSource {

    fun getChatMessages(vodId: String, afterMillis: Long, beforeMillis: Long): Single<List<ChatMessage>>
}
