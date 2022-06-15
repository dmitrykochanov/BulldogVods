package com.dmko.bulldogvods.features.chat.presentation.mapping

import android.graphics.drawable.Animatable
import android.text.SpannableStringBuilder
import androidx.core.graphics.toColorInt
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import com.dmko.bulldogvods.app.common.extensions.toColorIntOrNull
import com.dmko.bulldogvods.app.common.image.spans.AutoSizeDrawableSpan
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessageWithDrawables
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUser
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem
import javax.inject.Inject

class ChatMessageToChatMessageItemMapper @Inject constructor() {

    fun map(messageWithDrawables: ChatMessageWithDrawables): ChatMessageItem {
        return ChatMessageItem(
            id = messageWithDrawables.message.id,
            text = buildSpannedString {
                appendBadges(messageWithDrawables)
                appendUserName(messageWithDrawables.message.user)
                appendTextWithEmotes(messageWithDrawables)
            },
            animatedDrawables = messageWithDrawables.drawables.values.filter { it is Animatable }
        )
    }

    private fun SpannableStringBuilder.appendBadges(messageWithDrawables: ChatMessageWithDrawables) {
        append("\u200B")
        messageWithDrawables.message.user.badges.forEach { badge ->
            val badgeDrawable = messageWithDrawables.drawables[badge.url]
            if (badgeDrawable != null) {
                inSpans(AutoSizeDrawableSpan(badgeDrawable)) {
                    append(" ")
                }
                append(" ")
            }
        }
    }

    private fun SpannableStringBuilder.appendUserName(user: ChatUser) {
        color(user.nameColorHex.toColorIntOrNull() ?: DEFAULT_USER_NAME_COLOR.toColorInt()) {
            append(user.name)
        }
        append(": ")
    }

    private fun SpannableStringBuilder.appendTextWithEmotes(messageWithDrawables: ChatMessageWithDrawables) {
        val words = messageWithDrawables.message.text.split(" ")
        words.forEachIndexed { index, word ->
            val emote = messageWithDrawables.message.emotes.firstOrNull { it.name == word }
            val emoteDrawable = emote?.url?.let(messageWithDrawables.drawables::get)
            if (emoteDrawable != null) {
                val isZeroWidth = if (emote.isZeroWidth) {
                    val previousWord = words.getOrNull(index - 1)
                    val previousEmote = messageWithDrawables.message.emotes.firstOrNull { it.name == previousWord }
                    val previousEmoteDrawable = previousEmote?.url?.let(messageWithDrawables.drawables::get)
                    previousEmoteDrawable != null && !previousEmote.isZeroWidth
                } else {
                    false
                }
                inSpans(AutoSizeDrawableSpan(emoteDrawable, isZeroWidth)) {
                    append(word)
                }
            } else {
                append(word)
            }
            append(" ")
        }
    }

    private companion object {

        private const val DEFAULT_USER_NAME_COLOR = "#DAA520"
    }
}
