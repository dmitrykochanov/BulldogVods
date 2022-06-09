package com.dmko.bulldogvods.features.chat.domain.entities

import android.graphics.drawable.Drawable

data class ChatMessageWithDrawables(
    val message: ChatMessage,
    val drawables: Map<String, Drawable>
)
