package com.dmko.bulldogvods.features.chat.domain.entities

data class ChatPosition(
    val landscapePosition: Landscape,
    val portraitPosition: Portrait,
    val isVisibleInLandscape: Boolean,
    val isVisibleInPortrait: Boolean
) {

    enum class Landscape {
        LEFT,
        RIGHT,
        LEFT_OVERLAY,
        RIGHT_OVERLAY,
        TOP_LEFT_OVERLAY,
        TOP_RIGHT_OVERLAY,
        BOTTOM_LEFT_OVERLAY,
        BOTTOM_RIGHT_OVERLAY
    }

    enum class Portrait {
        TOP, BOTTOM
    }
}
