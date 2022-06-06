package com.dmko.bulldogvods.features.chat.presentation.mapping

import android.text.Spanned
import androidx.core.graphics.toColorInt
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.dmko.bulldogvods.app.common.extensions.toColorIntOrNull
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition
import com.dmko.bulldogvods.features.chat.domain.entities.ChatEmote
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUserBadge
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem.SpanImage
import javax.inject.Inject

class ChatMessageToChatMessageItemMapper @Inject constructor() {

    fun map(chatMessage: ChatMessage): ChatMessageItem {
        val text = getMessageText(chatMessage)
        val badgesImages = getUserBadgesSpanImages(chatMessage.user.badges)
        val emoteImages = getEmotesSpanImages(text, chatMessage.emotes)
        return ChatMessageItem(
            id = chatMessage.id,
            text = text,
            spanImages = badgesImages + emoteImages
        )
    }

    private fun getMessageText(chatMessage: ChatMessage): Spanned {
        return buildSpannedString {
            repeat(chatMessage.user.badges.size) {
                append(" ")
            }
            repeat(chatMessage.user.badges.size * SPACES_BETWEEN_BADGES) {
                append(" ")
            }
            color(chatMessage.user.nameColorHex.toColorIntOrNull() ?: DEFAULT_USER_NAME_COLOR.toColorInt()) {
                append(chatMessage.user.name)
            }
            append(": ")
            append(chatMessage.text)
        }
    }

    private fun getUserBadgesSpanImages(userBadges: List<ChatUserBadge>): List<SpanImage> {
        return userBadges.mapIndexed { index, chatUserBadge ->
            SpanImage(
                url = chatUserBadge.urls.first(),
                spanPositions = listOf(
                    SpanPosition(
                        startIndexInclusive = index * (SPACES_BETWEEN_BADGES + 1),
                        endIndexExclusive = index * (SPACES_BETWEEN_BADGES + 1) + 1
                    )
                )
            )
        }
    }

    private fun getEmotesSpanImages(text: Spanned, emotes: List<ChatEmote>): List<SpanImage> {
        return emotes.map { emote ->
            SpanImage(
                url = emote.urls.first(),
                spanPositions = findEmotePositions(text, emote)
            )
        }
    }

    private fun findEmotePositions(text: Spanned, emote: ChatEmote): List<SpanPosition> {
        return EMOTE_REGEX.format(emote.name).toRegex()
            .findAll(text)
            .map { matchResult ->
                SpanPosition(
                    startIndexInclusive = matchResult.range.first,
                    endIndexExclusive = matchResult.range.last + 1,
                    shouldOverlapWithPrevious = emote.isZeroWidth
                )
            }
            .toList()
    }

    private companion object {

        private const val SPACES_BETWEEN_BADGES = 1
        private const val EMOTE_REGEX = "(?<= )\\Q%s\\E(?=( |\$))"
        private const val DEFAULT_USER_NAME_COLOR = "#DAA520"
    }
}
