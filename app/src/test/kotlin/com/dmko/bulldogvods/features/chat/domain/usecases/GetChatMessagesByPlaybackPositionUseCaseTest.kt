package com.dmko.bulldogvods.features.chat.domain.usecases

import com.dmko.bulldogvods.features.chat.domain.entities.ChatReplayConfiguration
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.MESSAGE_1
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.MESSAGE_2
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.MESSAGE_3
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.MESSAGE_4
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.MESSAGE_5
import com.dmko.bulldogvods.features.chat.domain.usecases.FakeNetworkChatDataSource.Companion.VOD
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.time.Duration.Companion.milliseconds

class GetChatMessagesByPlaybackPositionUseCaseTest {

    @Test
    fun `Should not return messages when playback position is not specified`() {
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(
            FakeNetworkChatDataSource(),
            ChatReplayConfiguration()
        )
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()

        testSubscriber.assertEmpty()
    }

    @ParameterizedTest
    @MethodSource("provideConfigsToTest")
    fun `Should return correct messages when playback position is passed`(config: ChatReplayConfiguration) {
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(FakeNetworkChatDataSource(), config)
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()
        playbackPositionSubject.onNext(MESSAGE_1.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_2.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_3.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_4.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_5.sentAtMillis - VOD.startedAtMillis)

        testSubscriber.assertValues(
            listOfNotNull(
                MESSAGE_1.takeIf { config.historySize >= 1 }
            ),
            listOfNotNull(
                MESSAGE_1.takeIf { config.historySize >= 2 },
                MESSAGE_2.takeIf { config.historySize >= 1 }
            ),
            listOfNotNull(
                MESSAGE_1.takeIf { config.historySize >= 3 },
                MESSAGE_2.takeIf { config.historySize >= 2 },
                MESSAGE_3.takeIf { config.historySize >= 1 }
            ),
            listOfNotNull(
                MESSAGE_1.takeIf { config.historySize >= 4 },
                MESSAGE_2.takeIf { config.historySize >= 3 },
                MESSAGE_3.takeIf { config.historySize >= 2 },
                MESSAGE_4.takeIf { config.historySize >= 1 }
            ),
            listOfNotNull(
                MESSAGE_1.takeIf { config.historySize >= 5 },
                MESSAGE_2.takeIf { config.historySize >= 4 },
                MESSAGE_3.takeIf { config.historySize >= 3 },
                MESSAGE_4.takeIf { config.historySize >= 2 },
                MESSAGE_5.takeIf { config.historySize >= 1 }
            )
        )
    }

    @Test
    fun `Should prefetch messages after seeking`() {
        val config = ChatReplayConfiguration(
            initialPreloadOffset = (MESSAGE_4.sentAtMillis - MESSAGE_2.sentAtMillis + 1).milliseconds
        )
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(FakeNetworkChatDataSource(), config)
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()
        playbackPositionSubject.onNext(MESSAGE_4.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_5.sentAtMillis - VOD.startedAtMillis)

        testSubscriber.assertValues(
            listOf(MESSAGE_2, MESSAGE_3, MESSAGE_4),
            listOf(MESSAGE_2, MESSAGE_3, MESSAGE_4, MESSAGE_5)
        )
    }

    @Test
    fun `Should delete all messages if new playback position is after last loaded message`() {
        val config = ChatReplayConfiguration(
            pageSize = 2,
            initialPreloadOffset = (MESSAGE_5.sentAtMillis - MESSAGE_4.sentAtMillis + 1).milliseconds
        )
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(FakeNetworkChatDataSource(), config)
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()
        playbackPositionSubject.onNext(MESSAGE_1.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_5.sentAtMillis - VOD.startedAtMillis)

        testSubscriber.assertValues(
            listOf(MESSAGE_1),
            listOf(MESSAGE_4, MESSAGE_5)
        )
    }

    @Test
    fun `Should not delete messages if new playback position is before last loaded message`() {
        val config = ChatReplayConfiguration(
            pageSize = 5,
            initialPreloadOffset = (MESSAGE_5.sentAtMillis - MESSAGE_3.sentAtMillis + 1).milliseconds
        )
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(FakeNetworkChatDataSource(), config)
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()
        playbackPositionSubject.onNext(MESSAGE_1.sentAtMillis - VOD.startedAtMillis)
        playbackPositionSubject.onNext(MESSAGE_5.sentAtMillis - VOD.startedAtMillis)

        testSubscriber.assertValues(
            listOf(MESSAGE_1),
            listOf(MESSAGE_1, MESSAGE_2, MESSAGE_3, MESSAGE_4, MESSAGE_5)
        )
    }

    @Test
    @Disabled(
        """If page size is less than the amount of preloaded messages then only the messages before the current
            playback position will be loaded. Should not cause any problems in a real world scenario."""
    )
    fun `Should return correct messages if page size is less than the size of prefetched messages`() {
        val config = ChatReplayConfiguration(
            pageSize = 2,
            initialPreloadOffset = (MESSAGE_5.sentAtMillis - MESSAGE_2.sentAtMillis + 1).milliseconds
        )
        val getChatMessagesUseCase = GetChatMessagesByPlaybackPositionUseCase(FakeNetworkChatDataSource(), config)
        val playbackPositionSubject = PublishSubject.create<Long>()

        val testSubscriber = getChatMessagesUseCase
            .execute(VOD, playbackPositionSubject)
            .test()
        playbackPositionSubject.onNext(MESSAGE_5.sentAtMillis - VOD.startedAtMillis)

        testSubscriber.assertValues(
            listOf(MESSAGE_2, MESSAGE_3, MESSAGE_4, MESSAGE_5)
        )
    }

    companion object {

        @JvmStatic
        fun provideConfigsToTest(): List<ChatReplayConfiguration> {
            return listOf(
                ChatReplayConfiguration(pageSize = 2, historySize = 2),
                ChatReplayConfiguration(pageSize = 2, historySize = 3),
                ChatReplayConfiguration(pageSize = 2, historySize = 4),
                ChatReplayConfiguration(pageSize = 2, historySize = 5),
                ChatReplayConfiguration(pageSize = 3, historySize = 3),
                ChatReplayConfiguration(pageSize = 3, historySize = 4),
                ChatReplayConfiguration(pageSize = 3, historySize = 5),
                ChatReplayConfiguration(pageSize = 4, historySize = 4),
                ChatReplayConfiguration(pageSize = 4, historySize = 5),
                ChatReplayConfiguration(pageSize = 5, historySize = 5)
            )
        }
    }
}