package com.dmko.bulldogvods.features.chat.presentation.entities

import android.graphics.drawable.Drawable
import android.text.Spanned

data class ChatMessageItem(
    val id: String,
    val text: Spanned,
    val drawables: Map<String, Drawable>
)
