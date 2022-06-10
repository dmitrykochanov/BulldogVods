package com.dmko.bulldogvods.features.chat.domain.usecases

import android.graphics.drawable.Drawable
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessageWithDrawables
import com.dmko.bulldogvods.features.chat.domain.entities.GetChatMessagesRequest
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetChatMessagesWithDrawablesUseCase @Inject constructor(
    private val networkChatDataSource: NetworkChatDataSource,
    private val imageLoader: ImageLoader
) {

    fun execute(request: GetChatMessagesRequest): Single<List<ChatMessageWithDrawables>> {
        return networkChatDataSource.getChatMessages(request)
            .toFlowable()
            .flatMapIterable { it }
            .flatMapSingle(::getMessageWithDrawables)
            .toList()
            .map { messages -> messages.sortedBy { it.message.sentAtMillis } }
    }

    private fun getMessageWithDrawables(message: ChatMessage): Single<ChatMessageWithDrawables> {
        val badgesMaybes = message.user.badges.map { badge ->
            imageLoader.load(badge.url)
                .map { drawable -> badge.url to drawable }
                .onErrorComplete()
        }
        val emotesMaybes = message.emotes.map { emote ->
            imageLoader.load(emote.url)
                .map { drawable -> emote.url to drawable }
                .onErrorComplete()
        }
        val drawableMaybes = badgesMaybes + emotesMaybes
        val messageWithNoDrawables = ChatMessageWithDrawables(message, emptyMap())
        return if (drawableMaybes.isEmpty()) {
            Single.just(messageWithNoDrawables)
        } else {
            Maybe.zip(badgesMaybes + emotesMaybes) { drawablesArray ->
                ChatMessageWithDrawables(
                    message = message,
                    drawables = getDrawablesMap(drawablesArray)
                )
            }
                .defaultIfEmpty(messageWithNoDrawables)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getDrawablesMap(drawablesArray: Array<Any>): Map<String, Drawable> {
        val drawablesMap = mutableMapOf<String, Drawable>()
        drawablesArray.forEach { pair ->
            val (url, drawable) = (pair as Pair<String, Drawable>)
            drawablesMap[url] = drawable
        }
        return drawablesMap
    }
}
