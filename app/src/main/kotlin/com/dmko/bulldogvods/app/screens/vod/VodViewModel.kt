package com.dmko.bulldogvods.app.screens.vod

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.extensions.getFlowable
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.app.common.resource.asResource
import com.dmko.bulldogvods.app.common.resource.mapResource
import com.dmko.bulldogvods.app.common.resource.switchMapResource
import com.dmko.bulldogvods.app.common.resource.unwrapResource
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.LongWrapper
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.app.screens.chapterchooser.ChapterChooserDialogEvent
import com.dmko.bulldogvods.app.screens.videosourcechooser.VideoSourceChooserDialogEvent
import com.dmko.bulldogvods.features.chat.data.local.LocalChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessageWithDrawables
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import com.dmko.bulldogvods.features.chat.domain.usecases.ReplayChatMessagesUseCase
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem
import com.dmko.bulldogvods.features.chat.presentation.mapping.ChatMessageToChatMessageItemMapper
import com.dmko.bulldogvods.features.chat.presentation.mapping.ChatTextSizeToSpMapper
import com.dmko.bulldogvods.features.vods.data.database.datasource.DatabaseVodsDataSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.selector.DefaultVideoSourceSelector
import com.dmko.bulldogvods.features.vods.presentation.player.ExoPlayerFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class VodViewModel @Inject constructor(
    private val replayChatMessagesUseCase: ReplayChatMessagesUseCase,
    private val chatMessageItemMapper: ChatMessageToChatMessageItemMapper,
    private val chatTextSizeToSpMapper: ChatTextSizeToSpMapper,
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
    private val networkVodsDataSource: NetworkVodsDataSource,
    private val databaseVodsDataSource: DatabaseVodsDataSource,
    private val localChatDataSource: LocalChatDataSource,
    private val defaultVideoSourceSelector: DefaultVideoSourceSelector,
    private val schedulers: Schedulers,
    exoPlayerFactory: ExoPlayerFactory
) : RxViewModel(), DefaultLifecycleObserver {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val exoPlayer = exoPlayerFactory.createExoPlayer()

    private val vodSettingsClickedSubject = PublishSubject.create<Unit>()
    private val playbackPositionSubject = PublishSubject.create<Long>()
    private val refreshSubject = PublishSubject.create<Unit>()
    private val refreshChatSubject = PublishSubject.create<Unit>()

    private val titleMutableLiveData = MutableLiveData<Resource<String>>()
    val titleLiveData: LiveData<Resource<String>> = titleMutableLiveData

    private val playerMutableLiveData = MutableLiveData<Resource<Player>>()
    val playerLiveData: LiveData<Resource<Player>> = playerMutableLiveData

    private val chatMessageItemsMutableLiveData = MutableLiveData<Resource<List<ChatMessageItem>>>()
    val chatMessageItemsLiveData: LiveData<Resource<List<ChatMessageItem>>> = chatMessageItemsMutableLiveData

    private val chatPositionMutableLiveData = MutableLiveData<ChatPosition>()
    val chatPositionLiveData: LiveData<ChatPosition> = chatPositionMutableLiveData

    private val chatTextSizeSpMutableLiveData = MutableLiveData<Float>()
    val chatTextSizeSpLiveData: LiveData<Float> = chatTextSizeSpMutableLiveData

    private val chatWidthPercentageMutableLiveData = MutableLiveData<Float>()
    val chatWidthPercentageLiveData: LiveData<Float> = chatWidthPercentageMutableLiveData

    private val keepScreenOnMutableLiveData = MutableLiveData<Boolean>()
    val keepScreenOnLiveData: LiveData<Boolean> = keepScreenOnMutableLiveData

    val isAutoScrollPausedLiveData: LiveData<Boolean> = savedStateHandle.getLiveData(ARG_IS_AUTO_SCROLL_PAUSED, false)

    private var updateChatDisposable: Disposable? = null

    init {
        setupPlayerStateListener()
        val startOffsetMillis = savedStateHandle.get<LongWrapper>(ARG_START_OFFSET_MILLIS)
        if (startOffsetMillis != null) {
            saveVodPlaybackPosition(startOffsetMillis.value)
        }

        val vodFlowable = refreshSubject.toFlowable(BackpressureStrategy.LATEST)
            .startWithItem(Unit)
            .switchMap { networkVodsDataSource.getVod(vodId).asResource() }
            .replay(1)
            .autoConnect()

        val savedVideoSourceUrlFlowable = savedStateHandle.getFlowable<String>(ARG_VIDEO_SOURCE_URL)
            .map { savedVideoSourceUrl -> Resource.Data(savedVideoSourceUrl) }

        val defaultVideoSourceUrlFlowable = vodFlowable
            .mapResource(::getDefaultVideoSourceUrl)
            .takeUntil(savedVideoSourceUrlFlowable)

        val selectedVideoSourceUrlFlowable = Flowable.merge(savedVideoSourceUrlFlowable, defaultVideoSourceUrlFlowable)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect()

        selectedVideoSourceUrlFlowable
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(::onVideoSourceUrlChanged)
            .disposeOnClear()

        databaseVodsDataSource.observeVodPlaybackPosition(vodId)
            .distinctUntilChanged()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(::onStartOffsetChanged)
            .disposeOnClear()

        vodSettingsClickedSubject.toFlowable(BackpressureStrategy.LATEST)
            .switchMapSingle { selectedVideoSourceUrlFlowable.unwrapResource().firstOrError() }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { selectedVideoSourceUrl ->
                navigationCommandDispatcher.dispatch(NavigationCommand.VodSettings(vodId, selectedVideoSourceUrl))
            }
            .disposeOnClear()

        vodFlowable
            .mapResource(Vod::title)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(titleMutableLiveData::setValue)
            .disposeOnClear()

        vodFlowable
            .switchMapResource(::getChatMessagesFlowable)
            .mapResource { chatMessages -> chatMessages.map(chatMessageItemMapper::map) }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatMessageItemsMutableLiveData::setValue)
            .disposeOnClear()

        localChatDataSource.chatPositionFlowable
            .distinctUntilChanged()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatPositionMutableLiveData::setValue)
            .disposeOnClear()

        localChatDataSource.chatTextSizeFlowable
            .distinctUntilChanged()
            .map(chatTextSizeToSpMapper::map)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatTextSizeSpMutableLiveData::setValue)
            .disposeOnClear()

        localChatDataSource.chatWidthPercentageFlowable
            .distinctUntilChanged()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatWidthPercentageMutableLiveData::setValue)
            .disposeOnClear()
    }

    private fun setupPlayerStateListener() {
        exoPlayer.addListener(
            object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        onVodPlaybackStarted()
                    } else {
                        onVodPlaybackStopped()
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    playbackPositionSubject.onNext(exoPlayer.currentPosition)
                }
            }
        )
    }

    private fun onVodPlaybackStarted() {
        keepScreenOnMutableLiveData.value = true
        updateChatDisposable = Flowable.interval(CHAT_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { playbackPositionSubject.onNext(exoPlayer.currentPosition) }
    }

    private fun onVodPlaybackStopped() {
        keepScreenOnMutableLiveData.value = false
        updateChatDisposable?.dispose()
    }

    private fun getChatMessagesFlowable(vod: Vod): Flowable<Resource<List<ChatMessageWithDrawables>>> {
        return refreshChatSubject.toFlowable(BackpressureStrategy.LATEST)
            .startWithItem(Unit)
            .switchMap { replayChatMessagesUseCase.execute(vod, playbackPositionSubject) }
    }

    private fun getDefaultVideoSourceUrl(vod: Vod): String {
        val readyVideoSources = vod.videoSources.filter(VideoSource::isReady)
        return defaultVideoSourceSelector.selectDefaultVideoSource(readyVideoSources).url
    }

    private fun onVideoSourceUrlChanged(videoSourceUrlResource: Resource<String>) {
        when (videoSourceUrlResource) {
            is Resource.Loading -> {
                playerMutableLiveData.value = Resource.Loading
                exoPlayer.removeMediaItem(0)
            }
            is Resource.Data -> {
                val videoSourceUrl = videoSourceUrlResource.data
                Timber.i("Switching video source to $videoSourceUrl")
                playerMutableLiveData.value = Resource.Data(exoPlayer)
                exoPlayer.setMediaItem(MediaItem.fromUri(videoSourceUrl), exoPlayer.currentPosition)
            }
            is Resource.Error -> {
                playerMutableLiveData.value = Resource.Error(videoSourceUrlResource.error)
                exoPlayer.removeMediaItem(0)
                Timber.e(videoSourceUrlResource.error, "Failed to load video sources")
            }
        }
    }

    private fun onStartOffsetChanged(startOffsetMillis: Long) {
        if (exoPlayer.currentPosition != startOffsetMillis) {
            exoPlayer.seekTo(startOffsetMillis)
        }
    }

    fun onBackClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.Back)
    }

    fun onVodSettingsClicked() {
        vodSettingsClickedSubject.onNext(Unit)
    }

    fun onVodChaptersClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.ChapterChooser(vodId))
    }

    fun onLandscapePlayerDoubleClicked() {
        localChatDataSource.chatPositionFlowable
            .firstOrError()
            .flatMapCompletable { chatPosition ->
                localChatDataSource.saveLandscapeChatVisibility(!chatPosition.isVisibleInLandscape)
            }
            .subscribeOn(schedulers.io)
            .subscribe()
            .disposeOnClear()
    }

    fun onPortraitPlayerDoubleClicked() {
        localChatDataSource.chatPositionFlowable
            .firstOrError()
            .flatMapCompletable { chatPosition ->
                localChatDataSource.savePortraitChatVisibility(!chatPosition.isVisibleInPortrait)
            }
            .subscribeOn(schedulers.io)
            .subscribe()
            .disposeOnClear()
    }

    fun onAutoScrollPaused() {
        savedStateHandle.set(ARG_IS_AUTO_SCROLL_PAUSED, true)
    }

    fun onAutoScrollResumed() {
        savedStateHandle.set(ARG_IS_AUTO_SCROLL_PAUSED, false)
    }

    fun onRetryClicked() {
        refreshSubject.onNext(Unit)
    }

    fun onRetryChatClicked() {
        refreshChatSubject.onNext(Unit)
    }

    @Subscribe
    fun onVodPlaybackSettingsDialogEvent(event: VideoSourceChooserDialogEvent) {
        savedStateHandle.set(ARG_VIDEO_SOURCE_URL, event.selectedVideoSourceUrl)
    }

    @Subscribe
    fun onChapterChooserDialogEvent(event: ChapterChooserDialogEvent) {
        saveVodPlaybackPosition(event.selectedStartOffset.inWholeMilliseconds)
        exoPlayer.playWhenReady = true
    }

    override fun onStart(owner: LifecycleOwner) {
        eventBus.register(this)
        exoPlayer.playWhenReady = savedStateHandle.get<Boolean>(ARG_IS_PLAYING) ?: true
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
        savedStateHandle.set(ARG_IS_PLAYING, exoPlayer.isPlaying || exoPlayer.currentMediaItem == null)
        saveVodPlaybackPosition(exoPlayer.currentPosition)
        exoPlayer.pause()
    }

    private fun saveVodPlaybackPosition(playbackPosition: Long) {
        databaseVodsDataSource.saveVodPlaybackPosition(vodId, playbackPosition)
            .subscribeOn(schedulers.io)
            .subscribe()
            .disposeOnClear()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    companion object {

        const val ARG_VOD_ID = "vod_id"
        const val ARG_START_OFFSET_MILLIS = "start_offset_millis"
        private const val ARG_VIDEO_SOURCE_URL = "video_source_url"
        private const val ARG_IS_PLAYING = "is_playing"
        private const val ARG_IS_AUTO_SCROLL_PAUSED = "is_auto_scroll_paused"

        private const val CHAT_UPDATE_INTERVAL_SECONDS = 1L
    }
}
