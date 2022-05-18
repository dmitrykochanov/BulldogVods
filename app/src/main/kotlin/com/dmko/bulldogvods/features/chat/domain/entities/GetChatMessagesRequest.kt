package com.dmko.bulldogvods.features.chat.domain.entities

data class GetChatMessagesRequest(
    val vodId: String,
    val limit: Int,
    val beforeMillis: Long,
    val afterMillis: Long
)
