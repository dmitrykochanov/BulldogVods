package com.dmko.bulldogvods.features.chat.domain.usecases

import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.entities.ChatReplayConfiguration
import com.dmko.bulldogvods.features.chat.domain.entities.GetChatMessagesRequest
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class GetChatMessagesByPlaybackPositionUseCase @Inject constructor(
    private val networkChatDataSource: NetworkChatDataSource,
    private val config: ChatReplayConfiguration
) {

    private val initialPreloadOffsetMillis = config.initialPreloadOffset.inWholeMilliseconds
    private val prefetchDelayMillis = config.prefetchDelay.inWholeMilliseconds

    fun execute(vod: Vod, playbackPositionSubject: PublishSubject<Long>): Flowable<List<ChatMessage>> {
        val loadedMessagesSubject = PublishSubject.create<List<ChatMessage>>()
        val loadedMessagesFlowable = loadedMessagesSubject.toFlowable(BackpressureStrategy.LATEST)
            .startWithItem(emptyList())
        val playbackPositionFlowable = playbackPositionSubject.toFlowable(BackpressureStrategy.LATEST)

        return playbackPositionFlowable.withLatestFrom(loadedMessagesFlowable, ::Pair)
            .concatMapSingle { pair ->
                val (playbackPosition, loadedMessages) = pair
                val clearedMessages = clearMessagesOnSeek(vod, playbackPosition, loadedMessages)
                loadMoreMessagesIfNeeded(vod, playbackPosition, clearedMessages)
                    .map { messages -> removeOutdatedMessages(vod, playbackPosition, messages) }
                    .doOnSuccess(loadedMessagesSubject::onNext)
                    .map { messages -> getMessagesToShow(vod, playbackPosition, messages) }
            }
            .distinctUntilChanged()
    }

    private fun loadMoreMessagesIfNeeded(
        vod: Vod,
        playbackPosition: Long,
        currentlyLoadedMessages: List<ChatMessage>
    ): Single<List<ChatMessage>> {
        requireNotNull(vod.endedAtMillis)
        val lastLoadedMessageSentAtMillis = currentlyLoadedMessages.lastOrNull()?.sentAtMillis
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val loadAfterMillis = when {
            lastLoadedMessageSentAtMillis == null -> vodPlaybackPosition - initialPreloadOffsetMillis
            lastLoadedMessageSentAtMillis - vodPlaybackPosition < prefetchDelayMillis -> lastLoadedMessageSentAtMillis
            else -> null
        }
        return if (loadAfterMillis != null) {
            val request = GetChatMessagesRequest(
                vodId = vod.id,
                limit = config.pageSize,
                beforeMillis = vod.endedAtMillis,
                afterMillis = loadAfterMillis
            )
            networkChatDataSource.getChatMessages(request)
                .map { loadedMessages -> currentlyLoadedMessages + loadedMessages }
        } else {
            Single.just(currentlyLoadedMessages)
        }
    }

    private fun clearMessagesOnSeek(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessage>
    ): List<ChatMessage> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val lastLoadedMessageSentAtMillis = loadedMessages.lastOrNull()?.sentAtMillis
        return if (lastLoadedMessageSentAtMillis != null && vodPlaybackPosition > lastLoadedMessageSentAtMillis) {
            emptyList()
        } else {
            loadedMessages
        }
    }

    private fun removeOutdatedMessages(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessage>
    ): List<ChatMessage> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val historyMessages = loadedMessages.filter { message -> message.sentAtMillis <= vodPlaybackPosition }
        val futureMessages = loadedMessages - historyMessages.toSet()
        return historyMessages.takeLast(config.historySize) + futureMessages
    }

    private fun getMessagesToShow(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessage>
    ): List<ChatMessage> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        return loadedMessages.filter { message -> message.sentAtMillis <= vodPlaybackPosition }
    }
}
