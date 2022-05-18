package com.dmko.bulldogvods.features.chat.domain.entities

data class ChatUser(
    val name: String,
    val nameColorHex: String,
    val badges: List<ChatUserBadge>
)
