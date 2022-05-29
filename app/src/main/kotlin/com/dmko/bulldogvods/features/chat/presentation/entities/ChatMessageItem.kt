package com.dmko.bulldogvods.features.chat.presentation.entities

import android.text.Spanned
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition

data class ChatMessageItem(
    val id: String,
    val text: Spanned,
    val spanImages: List<SpanImage>
) {

    data class SpanImage(
        val url: String,
        val spanPositions: List<SpanPosition>
    )
}
