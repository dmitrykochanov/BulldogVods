package com.dmko.bulldogvods.features.chat.data.network.mapping

import apollo.ChatMessagesQuery
import com.dmko.bulldogvods.features.chat.domain.entities.ChatEmote
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUser
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUserBadge
import com.dmko.bulldogvods.features.vods.data.network.mapping.DateStringToTimestampMapper
import javax.inject.Inject

class MessageSchemaToChatMessageMapper @Inject constructor(
    private val dateStringToTimestampMapper: DateStringToTimestampMapper
) {

    fun map(messageSchema: ChatMessagesQuery.Message): ChatMessage {
        return ChatMessage(
            id = messageSchema.id as String,
            text = messageSchema.content,
            sentAtMillis = dateStringToTimestampMapper.map(messageSchema.timestamp as String),
            user = ChatUser(
                name = messageSchema.twitch.display_name,
                nameColorHex = messageSchema.twitch.color,
                badges = messageSchema.badges.map(::mapBadge)
            ),
            emotes = messageSchema.emotes.map(::mapEmote)
        )
    }

    private fun mapBadge(badgeSchema: ChatMessagesQuery.Badge): ChatUserBadge {
        return ChatUserBadge(
            name = badgeSchema.name,
            url = badgeSchema.urls.first()
        )
    }

    private fun mapEmote(emoteSchema: ChatMessagesQuery.Emote): ChatEmote {
        return ChatEmote(
            name = emoteSchema.name,
            isZeroWidth = emoteSchema.zero_width,
            url = emoteSchema.urls.first()
        )
    }
}
