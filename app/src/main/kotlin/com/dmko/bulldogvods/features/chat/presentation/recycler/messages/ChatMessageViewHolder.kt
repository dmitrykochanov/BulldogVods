package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.extensions.toColorIntOrNull
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatEmote
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUserBadge

class ChatMessageViewHolder(
    private val binding: ListItemChatMessageBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindChatMessage(chatMessage: ChatMessage) {
        val context = binding.root.context
        val defaultUserNameColor = ContextCompat.getColor(context, R.color.golden_grass)
        binding.messageTextView.text = buildSpannedString {
            repeat(chatMessage.user.badges.size * 2) {
                append(" ")
            }
            color(chatMessage.user.nameColorHex.toColorIntOrNull() ?: defaultUserNameColor) {
                append(chatMessage.user.name)
            }
            append(": ")
            append(chatMessage.text)
        }
        loadUserBadges(chatMessage.user.badges)
        loadEmotes(chatMessage.emotes)
    }

    private fun loadUserBadges(userBadges: List<ChatUserBadge>) {
        userBadges.forEachIndexed { index, chatUserBadge ->
            imageLoader.loadIntoSpan(
                url = chatUserBadge.urls.first(),
                spanPosition = SpanPosition(index * 2, index * 2 + 1),
                target = binding.messageTextView
            )
        }
    }

    private fun loadEmotes(emotes: List<ChatEmote>) {
        emotes.forEach { emote ->
            imageLoader.loadIntoSpans(
                url = emote.urls.first(),
                spanPositions = findEmotePositions(emote),
                target = binding.messageTextView
            )
        }
    }

    private fun findEmotePositions(emote: ChatEmote): List<SpanPosition> {
        return "(?<= )\\Q${emote.name}\\E(?=( |\$))".toRegex()
            .findAll(binding.messageTextView.text)
            .map { matchResult ->
                SpanPosition(
                    startIndexInclusive = matchResult.range.first,
                    endIndexExclusive = matchResult.range.last + 1,
                    shouldOverlapWithPrevious = emote.isZeroWidth
                )
            }
            .toList()
    }

    fun onViewRecycled() {
        imageLoader.dispose(binding.messageTextView)
    }
}
