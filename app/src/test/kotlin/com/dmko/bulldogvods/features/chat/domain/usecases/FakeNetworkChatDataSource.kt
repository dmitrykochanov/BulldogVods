package com.dmko.bulldogvods.features.chat.domain.usecases

import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatUser
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodState
import io.reactivex.rxjava3.core.Single
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class FakeNetworkChatDataSource : NetworkChatDataSource {

    override fun getChatMessages(vodId: String, afterMillis: Long, beforeMillis: Long): Single<List<ChatMessage>> {
        val messages = listOf(MESSAGE_1, MESSAGE_2, MESSAGE_3, MESSAGE_4, MESSAGE_5)
            .filter { it.sentAtMillis > afterMillis }
            .filter { it.sentAtMillis < beforeMillis }
        return Single.just(messages)
    }

    companion object {

        private val USER_1 = ChatUser("", "", emptyList())

        val VOD = Vod(
            id = "1",
            title = "",
            startedAtMillis = 10.hours.inWholeMilliseconds,
            endedAtMillis = 20.hours.inWholeMilliseconds,
            state = VodState.Ready(5.hours),
            chapters = emptyList(),
            videoSources = emptyList()
        )

        val MESSAGE_1 = ChatMessage(
            id = "1",
            text = "",
            sentAtMillis = VOD.startedAtMillis,
            user = USER_1,
            emotes = emptyList()
        )
        val MESSAGE_2 = ChatMessage(
            id = "2",
            text = "",
            sentAtMillis = VOD.startedAtMillis + 5.seconds.inWholeMilliseconds,
            user = USER_1,
            emotes = emptyList()
        )
        val MESSAGE_3 = ChatMessage(
            id = "3",
            text = "",
            sentAtMillis = VOD.startedAtMillis + 10.seconds.inWholeMilliseconds,
            user = USER_1,
            emotes = emptyList()
        )
        val MESSAGE_4 = ChatMessage(
            id = "4",
            text = "",
            sentAtMillis = VOD.startedAtMillis + 15.seconds.inWholeMilliseconds,
            user = USER_1,
            emotes = emptyList()
        )
        val MESSAGE_5 = ChatMessage(
            id = "5",
            text = "",
            sentAtMillis = VOD.startedAtMillis + 20.seconds.inWholeMilliseconds,
            user = USER_1,
            emotes = emptyList()
        )
    }
}
