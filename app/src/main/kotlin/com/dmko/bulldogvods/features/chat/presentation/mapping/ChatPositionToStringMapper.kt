package com.dmko.bulldogvods.features.chat.presentation.mapping

import androidx.annotation.StringRes
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import javax.inject.Inject

class ChatPositionToStringMapper @Inject constructor() {

    @StringRes
    fun map(landscapePosition: ChatPosition.Landscape): Int {
        return when (landscapePosition) {
            ChatPosition.Landscape.LEFT -> R.string.chat_position_left
            ChatPosition.Landscape.RIGHT -> R.string.chat_position_right
            ChatPosition.Landscape.LEFT_OVERLAY -> R.string.chat_position_left_overlay
            ChatPosition.Landscape.RIGHT_OVERLAY -> R.string.chat_position_right_overlay
            ChatPosition.Landscape.TOP_LEFT_OVERLAY -> R.string.chat_position_top_left_overlay
            ChatPosition.Landscape.TOP_RIGHT_OVERLAY -> R.string.chat_position_top_right_overlay
            ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY -> R.string.chat_position_bottom_left_overlay
            ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY -> R.string.chat_position_bottom_right_overlay
        }
    }

    @StringRes
    fun map(portraitPosition: ChatPosition.Portrait): Int {
        return when (portraitPosition) {
            ChatPosition.Portrait.TOP -> R.string.chat_position_top
            ChatPosition.Portrait.BOTTOM -> R.string.chat_position_bottom
        }
    }
}
