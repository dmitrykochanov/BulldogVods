package com.dmko.bulldogvods.features.chat.domain.entities

data class ChatEmote(
    val name: String,
    val isZeroWidth: Boolean,
    val urls: List<String>
)
