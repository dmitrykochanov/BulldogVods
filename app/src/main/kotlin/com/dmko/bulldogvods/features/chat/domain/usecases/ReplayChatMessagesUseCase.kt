package com.dmko.bulldogvods.features.chat.domain.usecases

import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessageWithDrawables
import com.dmko.bulldogvods.features.chat.domain.entities.ChatReplayConfig
import com.dmko.bulldogvods.features.chat.domain.entities.GetChatMessagesRequest
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class ReplayChatMessagesUseCase @Inject constructor(
    private val getChatMessagesWithDrawablesUseCase: GetChatMessagesWithDrawablesUseCase,
    private val config: ChatReplayConfig
) {

    private val initialPreloadOffsetMillis = config.initialPreloadOffset.inWholeMilliseconds
    private val prefetchDelayMillis = config.prefetchDelay.inWholeMilliseconds

    fun execute(vod: Vod, playbackPositionSubject: PublishSubject<Long>): Flowable<List<ChatMessageWithDrawables>> {
        val loadedMessagesSubject = BehaviorSubject.createDefault<List<ChatMessageWithDrawables>>(emptyList())
        val loadedMessagesFlowable = loadedMessagesSubject.toFlowable(BackpressureStrategy.LATEST)
        val playbackPositionFlowable = playbackPositionSubject.toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .map { playbackPosition -> playbackPosition + config.playbackPositionOffset.inWholeMilliseconds }
        val messagesFlowable = playbackPositionFlowable
            .concatMapSingle { playbackPosition ->
                loadedMessagesFlowable.firstOrError()
                    .map { loadedMessages -> clearMessagesOnSeek(vod, playbackPosition, loadedMessages) }
                    .flatMap { loadedMessages -> loadMoreMessagesIfNeeded(vod, playbackPosition, loadedMessages) }
                    .map { loadedMessages -> removeOutdatedMessages(vod, playbackPosition, loadedMessages) }
                    .doOnSuccess(loadedMessagesSubject::onNext)
            }
        return playbackPositionFlowable.withLatestFrom(messagesFlowable) { playbackPosition, loadedMessages ->
            getMessagesToShow(vod, playbackPosition, loadedMessages)
        }
            .distinctUntilChanged()
    }

    private fun loadMoreMessagesIfNeeded(
        vod: Vod,
        playbackPosition: Long,
        currentlyLoadedMessages: List<ChatMessageWithDrawables>
    ): Single<List<ChatMessageWithDrawables>> {
        requireNotNull(vod.endedAtMillis)
        val lastLoadedMessageSentAtMillis = currentlyLoadedMessages.lastOrNull()?.message?.sentAtMillis
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val loadAfterMillis = when {
            lastLoadedMessageSentAtMillis == null -> {
                vodPlaybackPosition - initialPreloadOffsetMillis
            }
            lastLoadedMessageSentAtMillis - vodPlaybackPosition < prefetchDelayMillis -> {
                lastLoadedMessageSentAtMillis + 1
            }
            else -> null
        }
        return if (loadAfterMillis != null) {
            val request = GetChatMessagesRequest(
                vodId = vod.id,
                limit = config.pageSize,
                beforeMillis = vod.endedAtMillis,
                afterMillis = loadAfterMillis
            )
            getChatMessagesWithDrawablesUseCase.execute(request)
                .map { loadedMessages -> currentlyLoadedMessages + loadedMessages }
        } else {
            Single.just(currentlyLoadedMessages)
        }
    }

    private fun clearMessagesOnSeek(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessageWithDrawables>
    ): List<ChatMessageWithDrawables> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val lastLoadedMessageSentAtMillis = loadedMessages.lastOrNull()?.message?.sentAtMillis
        val firstLoadedMessageSentAtMillis = loadedMessages.firstOrNull()?.message?.sentAtMillis
        return if (lastLoadedMessageSentAtMillis != null && vodPlaybackPosition > lastLoadedMessageSentAtMillis) {
            emptyList()
        } else if (firstLoadedMessageSentAtMillis != null && vodPlaybackPosition < firstLoadedMessageSentAtMillis) {
            emptyList()
        } else {
            loadedMessages
        }
    }

    private fun removeOutdatedMessages(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessageWithDrawables>
    ): List<ChatMessageWithDrawables> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        val historyMessages = loadedMessages.filter { message -> message.message.sentAtMillis <= vodPlaybackPosition }
        val futureMessages = loadedMessages.filter { message -> message.message.sentAtMillis > vodPlaybackPosition }
        return historyMessages.takeLast(config.historySize) + futureMessages
    }

    private fun getMessagesToShow(
        vod: Vod,
        playbackPosition: Long,
        loadedMessages: List<ChatMessageWithDrawables>
    ): List<ChatMessageWithDrawables> {
        val vodPlaybackPosition = vod.startedAtMillis + playbackPosition
        return loadedMessages.filter { message -> message.message.sentAtMillis <= vodPlaybackPosition }
    }
}
