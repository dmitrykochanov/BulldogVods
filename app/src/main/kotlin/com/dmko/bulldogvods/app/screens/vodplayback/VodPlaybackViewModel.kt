package com.dmko.bulldogvods.app.screens.vodplayback

import android.content.Context
import androidx.lifecycle.*
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.rx.getFlowable
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.app.screens.chapterchooser.ChapterChooserDialogEvent
import com.dmko.bulldogvods.app.screens.vodplaybacksettings.VodPlaybackSettingsDialogEvent
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
import io.reactivex.rxjava3.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class VodPlaybackViewModel @Inject constructor(
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
    private val networkVodsDataSource: NetworkVodsDataSource,
    private val defaultVideoSourceSelector: DefaultVideoSourceSelector,
    schedulers: Schedulers,
    @ApplicationContext context: Context
) : RxViewModel(), DefaultLifecycleObserver {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val exoPlayer = ExoPlayer.Builder(context.applicationContext).build().also(ExoPlayer::prepare)
    private val vodPlaybackSettingsClickedSubject = PublishSubject.create<Unit>()

    val playerLiveData: LiveData<Player> = MutableLiveData(exoPlayer)

    init {
        val selectedVideoSourceUrlFlowable = createSelectedVideoSourceUrlFlowable()

        selectedVideoSourceUrlFlowable
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { videoSourceUrl ->
                exoPlayer.setMediaItem(MediaItem.fromUri(videoSourceUrl), exoPlayer.currentPosition)
            }
            .disposeOnClear()

        savedStateHandle.getFlowable<Long>(ARG_START_OFFSET_MILLIS)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { startOffsetMillis ->
                if (exoPlayer.currentPosition != startOffsetMillis) {
                    exoPlayer.seekTo(startOffsetMillis)
                }
            }
            .disposeOnClear()

        vodPlaybackSettingsClickedSubject.toFlowable(BackpressureStrategy.LATEST)
            .switchMapSingle { selectedVideoSourceUrlFlowable.firstOrError() }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { selectedVideoSourceUrl ->
                navigationCommandDispatcher.dispatch(NavigationCommand.VodPlaybackSettings(vodId, selectedVideoSourceUrl))
            }
            .disposeOnClear()
    }

    private fun createSelectedVideoSourceUrlFlowable(): Flowable<String> {
        val savedVideoSourceUrlFlowable = savedStateHandle.getFlowable<String>(ARG_VIDEO_SOURCE_URL)

        val defaultVideoSourceUrlFlowable = networkVodsDataSource.getVod(vodId)
            .map(Vod::videoSources)
            .map { videoSources -> videoSources.filter(VideoSource::isReady) }
            .map(defaultVideoSourceSelector::selectDefaultVideoSource)
            .map(VideoSource::url)
            .toFlowable()
            .takeUntil(savedVideoSourceUrlFlowable)

        return Flowable.merge(savedVideoSourceUrlFlowable, defaultVideoSourceUrlFlowable)
            .replay(1)
            .autoConnect()
    }

    fun onVodPlaybackSettingsClicked() {
        vodPlaybackSettingsClickedSubject.onNext(Unit)
    }

    fun onVodChaptersClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.ChapterChooser(vodId))
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
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
        savedStateHandle.set(ARG_IS_PLAYING, exoPlayer.isPlaying)
        savedStateHandle.set(ARG_START_OFFSET_MILLIS, exoPlayer.currentPosition)
        exoPlayer.pause()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
        private const val ARG_START_OFFSET_MILLIS = "start_offset_millis"
        private const val ARG_VIDEO_SOURCE_URL = "video_source_url"
        private const val ARG_IS_PLAYING = "is_playing"
    }
}
