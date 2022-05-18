package com.dmko.bulldogvods.features.chat.domain.entities

data class ChatMessage(
    val id: String,
    val text: String,
    val sentAtMillis: Long,
    val user: ChatUser,
    val emotes: List<ChatEmote>
)
