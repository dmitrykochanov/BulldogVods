package com.dmko.bulldogvods.features.chat.presentation.mapping

import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import javax.inject.Inject

class ChatTextSizeToSpMapper @Inject constructor() {

    fun map(chatTextSize: ChatTextSize): Float {
        return when (chatTextSize) {
            ChatTextSize.SMALL -> 11f
            ChatTextSize.NORMAL -> 14f
            ChatTextSize.LARGE -> 16f
            ChatTextSize.HUGE -> 18f
        }
    }
}
