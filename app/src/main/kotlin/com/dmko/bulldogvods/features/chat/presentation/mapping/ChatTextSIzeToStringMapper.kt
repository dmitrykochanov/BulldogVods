package com.dmko.bulldogvods.features.chat.presentation.mapping

import androidx.annotation.StringRes
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import javax.inject.Inject

class ChatTextSIzeToStringMapper @Inject constructor() {

    @StringRes
    fun map(size: ChatTextSize): Int {
        return when (size) {
            ChatTextSize.SMALL -> R.string.chat_text_size_small
            ChatTextSize.NORMAL -> R.string.chat_text_size_normal
            ChatTextSize.LARGE -> R.string.chat_text_size_large
            ChatTextSize.HUGE -> R.string.chat_text_size_huge
        }
    }
}
