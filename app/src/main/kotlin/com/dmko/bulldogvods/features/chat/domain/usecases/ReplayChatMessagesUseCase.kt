package com.dmko.bulldogvods.features.chat.domain.usecases

import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.app.common.resource.asResource
import com.dmko.bulldogvods.app.common.resource.map
import com.dmko.bulldogvods.app.common.resource.switchMapResource
import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessageWithDrawables
import com.dmko.bulldogvods.features.chat.domain.entities.ChatReplayConfig
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class ReplayChatMessagesUseCase @Inject constructor(
    private val networkChatDataSource: NetworkChatDataSource,
    private val getChatMessagesWithDrawablesUseCase: GetChatMessagesWithDrawablesUseCase,
    private val config: ChatReplayConfig
) {

    fun execute(
        vod: Vod,
        playbackPositionSubject: PublishSubject<Long>
    ): Flowable<Resource<List<ChatMessageWithDrawables>>> {
        val playbackPositionFlowable = playbackPositionSubject.toFlowable(BackpressureStrategy.LATEST)
            .distinctUntilChanged()
            .map { playbackPosition ->
                playbackPosition + vod.startedAtMillis + config.playbackPositionOffset.inWholeMilliseconds
            }
        val messagesFlowable = playbackPositionFlowable
            .scan(LongRange.EMPTY, ::updateMessagesTimeRange)
            .skip(1)
            .distinctUntilChanged()
            .scan(
                Flowable.just(emptyList<ChatMessageWithDrawables>()).asResource()
            ) { currentMessagesFlowable, messagesTimeRange ->
                syncTimeRangeWithCurrentMessagesFlowable(currentMessagesFlowable, messagesTimeRange, vod)
            }
            .skip(1)
            .concatMap { it }
        return Flowable.combineLatest(playbackPositionFlowable, messagesFlowable, ::filterMessagesToShow)
            .distinctUntilChanged()
    }

    private fun updateMessagesTimeRange(currentRange: LongRange, vodPlaybackPosition: Long): LongRange {
        val pageSizeMillis = config.pageSize.inWholeMilliseconds
        val prefetchDelayMillis = config.prefetchDelay.inWholeMilliseconds
        val initialPreloadOffsetMillis = config.initialPreloadOffset.inWholeMilliseconds
        return when {
            currentRange.isEmpty() ||
                    vodPlaybackPosition < currentRange.first ||
                    vodPlaybackPosition > currentRange.last -> {
                (vodPlaybackPosition - initialPreloadOffsetMillis)..(vodPlaybackPosition + pageSizeMillis)
            }
            currentRange.last - vodPlaybackPosition < prefetchDelayMillis -> {
                val last = currentRange.last + pageSizeMillis
                val first = max(currentRange.first, last - config.historySize.inWholeMilliseconds)
                first..last
            }
            vodPlaybackPosition - currentRange.first < prefetchDelayMillis -> {
                val first = currentRange.first - pageSizeMillis
                val last = min(currentRange.last, first + config.historySize.inWholeMilliseconds)
                first..last
            }
            else -> currentRange
        }
    }

    private fun syncTimeRangeWithCurrentMessagesFlowable(
        currentMessagesFlowable: Flowable<Resource<List<ChatMessageWithDrawables>>>,
        messagesTimeRange: LongRange,
        vod: Vod
    ): Flowable<Resource<List<ChatMessageWithDrawables>>> {
        return currentMessagesFlowable.switchMapResource { currentMessages ->
            syncTimeRangeWithCurrentMessages(currentMessages, messagesTimeRange, vod)
        }
            .replay(1)
            .autoConnect()
    }

    private fun syncTimeRangeWithCurrentMessages(
        currentMessages: List<ChatMessageWithDrawables>,
        messagesTimeRange: LongRange,
        vod: Vod
    ): Flowable<Resource<List<ChatMessageWithDrawables>>> {
        val firstMessageSentAt = currentMessages.firstOrNull()?.message?.sentAtMillis
        val lastMessageSentAt = currentMessages.lastOrNull()?.message?.sentAtMillis
        return when {
            firstMessageSentAt == null ||
                    lastMessageSentAt == null ||
                    messagesTimeRange.first > lastMessageSentAt ||
                    messagesTimeRange.last < firstMessageSentAt -> {
                loadMessages(vod.id, messagesTimeRange.first, messagesTimeRange.last).asResource()
            }
            else -> {
                val messagesInRange = currentMessages.filter { it.message.sentAtMillis in messagesTimeRange }
                val prependMessagesSingle = loadMessages(vod.id, messagesTimeRange.first, firstMessageSentAt - 1)
                val appendMessagesSingle = loadMessages(vod.id, lastMessageSentAt + 1, messagesTimeRange.last)
                Single.zip(prependMessagesSingle, appendMessagesSingle) { prependMessages, appendMessages ->
                    prependMessages + messagesInRange + appendMessages
                }
                    .asResource()
                    .filter { it !is Resource.Loading }
            }
        }
    }

    private fun loadMessages(
        vodId: String,
        afterMillis: Long,
        beforeMillis: Long
    ): Single<List<ChatMessageWithDrawables>> {
        return networkChatDataSource.getChatMessages(vodId, afterMillis, beforeMillis)
            .flatMap(getChatMessagesWithDrawablesUseCase::execute)
    }

    private fun filterMessagesToShow(
        playbackPosition: Long,
        loadedMessagesResource: Resource<List<ChatMessageWithDrawables>>
    ): Resource<List<ChatMessageWithDrawables>> {
        return loadedMessagesResource.map { loadedMessages ->
            loadedMessages.filter { message -> message.message.sentAtMillis <= playbackPosition }
        }
    }
}
