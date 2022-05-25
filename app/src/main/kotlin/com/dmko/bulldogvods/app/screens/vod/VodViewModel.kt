package com.dmko.bulldogvods.app.screens.vod

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.app.common.resource.asResource
import com.dmko.bulldogvods.app.common.resource.mapResource
import com.dmko.bulldogvods.app.common.resource.switchMapResource
import com.dmko.bulldogvods.app.common.resource.unwrapResource
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.rx.getFlowable
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.app.screens.chapterchooser.ChapterChooserDialogEvent
import com.dmko.bulldogvods.app.screens.vodplaybacksettings.VodPlaybackSettingsDialogEvent
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage
import com.dmko.bulldogvods.features.chat.domain.usecases.GetChatMessagesByPlaybackPositionUseCase
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.selector.DefaultVideoSourceSelector
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getChatMessagesByPlaybackPositionUseCase: GetChatMessagesByPlaybackPositionUseCase,
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
    private val networkVodsDataSource: NetworkVodsDataSource,
    private val defaultVideoSourceSelector: DefaultVideoSourceSelector,
    private val schedulers: Schedulers,
    @ApplicationContext context: Context
) : RxViewModel(), DefaultLifecycleObserver {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val exoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setSeekBackIncrementMs(SEEK_INCREMENT_MILLIS)
        .setSeekForwardIncrementMs(SEEK_INCREMENT_MILLIS)
        .build()
        .also(ExoPlayer::prepare)

    private val vodPlaybackSettingsClickedSubject = PublishSubject.create<Unit>()
    private val playbackPositionSubject = PublishSubject.create<Long>()
    private val refreshSubject = PublishSubject.create<Unit>()
    private val refreshChatSubject = PublishSubject.create<Unit>()

    private val playerMutableLiveData = MutableLiveData<Resource<Player>>()
    val playerLiveData: LiveData<Resource<Player>> = playerMutableLiveData

    private val chatMessagesMutableLiveData = MutableLiveData<Resource<List<ChatMessage>>>()
    val chatMessagesLiveData: LiveData<Resource<List<ChatMessage>>> = chatMessagesMutableLiveData

    val isAutoScrollPausedLiveData: LiveData<Boolean> = savedStateHandle.getLiveData(ARG_IS_AUTO_SCROLL_PAUSED, false)

    private var updateChatDisposable: Disposable? = null

    init {
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

        savedStateHandle.getFlowable<Long>(ARG_START_OFFSET_MILLIS)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(::onStartOffsetChanged)
            .disposeOnClear()

        vodPlaybackSettingsClickedSubject.toFlowable(BackpressureStrategy.LATEST)
            .switchMapSingle { selectedVideoSourceUrlFlowable.unwrapResource().firstOrError() }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { selectedVideoSourceUrl ->
                navigationCommandDispatcher.dispatch(
                    NavigationCommand.VodPlaybackSettings(vodId, selectedVideoSourceUrl)
                )
            }
            .disposeOnClear()

        vodFlowable
            .switchMapResource { vod ->
                refreshChatSubject.toFlowable(BackpressureStrategy.LATEST)
                    .startWithItem(Unit)
                    .switchMap {
                        getChatMessagesByPlaybackPositionUseCase.execute(vod, playbackPositionSubject).asResource()
                    }
            }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatMessagesMutableLiveData::setValue)
            .disposeOnClear()
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
                Timber.e(videoSourceUrlResource.error, "Failed to load video sources")
                playerMutableLiveData.value = Resource.Error(videoSourceUrlResource.error)
                exoPlayer.removeMediaItem(0)
            }
        }
    }

    private fun onStartOffsetChanged(startOffsetMillis: Long) {
        if (exoPlayer.currentPosition != startOffsetMillis) {
            exoPlayer.seekTo(startOffsetMillis)
        }
    }

    fun onVodPlaybackSettingsClicked() {
        vodPlaybackSettingsClickedSubject.onNext(Unit)
    }

    fun onVodChaptersClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.ChapterChooser(vodId))
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
    fun onVodPlaybackSettingsDialogEvent(event: VodPlaybackSettingsDialogEvent) {
        savedStateHandle.set(ARG_VIDEO_SOURCE_URL, event.selectedVideoSourceUrl)
    }

    @Subscribe
    fun onChapterChooserDialogEvent(event: ChapterChooserDialogEvent) {
        savedStateHandle.set(ARG_START_OFFSET_MILLIS, event.selectedStartOffset.inWholeMilliseconds)
        exoPlayer.playWhenReady = true
    }

    override fun onStart(owner: LifecycleOwner) {
        eventBus.register(this)
        exoPlayer.playWhenReady = savedStateHandle.get<Boolean>(ARG_IS_PLAYING) ?: true
        updateChatDisposable = Flowable.interval(CHAT_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { playbackPositionSubject.onNext(exoPlayer.currentPosition) }
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
        savedStateHandle.set(ARG_IS_PLAYING, exoPlayer.isPlaying || exoPlayer.currentMediaItem == null)
        savedStateHandle.set(ARG_START_OFFSET_MILLIS, exoPlayer.currentPosition)
        exoPlayer.pause()
        updateChatDisposable?.dispose()
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
        private const val SEEK_INCREMENT_MILLIS = 10_000L
    }
}
