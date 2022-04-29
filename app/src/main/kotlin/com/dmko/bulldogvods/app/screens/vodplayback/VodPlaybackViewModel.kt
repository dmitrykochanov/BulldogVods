package com.dmko.bulldogvods.app.screens.vodplayback

import android.content.Context
import androidx.lifecycle.*
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.selector.DefaultVideoSourceSelector
import com.dmko.bulldogvods.features.vods.presentation.events.VideoSourceSelectedEvent
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
    private val navigationEventDispatcher: NavigationEventDispatcher,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
    networkVodsDataSource: NetworkVodsDataSource,
    defaultVideoSourceSelector: DefaultVideoSourceSelector,
    schedulers: Schedulers,
    @ApplicationContext context: Context
) : RxViewModel(), DefaultLifecycleObserver {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val startOffsetMillis = requireNotNull(savedStateHandle.get<Long>(ARG_START_OFFSET_MILLIS))

    private val exoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    private val vodPlaybackSettingsClickedSubject = PublishSubject.create<Unit>()
    private val userSelectedVideoSourceUrlSubject = PublishSubject.create<String>()

    private val playerMutableLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player> = playerMutableLiveData

    init {
        playerMutableLiveData.value = exoPlayer
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        val defaultVideoSourceUrlFlowable = networkVodsDataSource.getVod(vodId)
            .map(Vod::videoSources)
            .map { videoSources -> videoSources.filter(VideoSource::isReady) }
            .map(defaultVideoSourceSelector::selectDefaultVideoSource)
            .map(VideoSource::url)
            .toFlowable()

        val userSelectedVideoSourceUrlFlowable = userSelectedVideoSourceUrlSubject
            .toFlowable(BackpressureStrategy.DROP)

        val selectedVideoSourceUrlFlowable = Flowable.merge(
            defaultVideoSourceUrlFlowable,
            userSelectedVideoSourceUrlFlowable
        )
            .replay(1)
            .autoConnect()

        selectedVideoSourceUrlFlowable
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { vodVideoSourceUrl ->
                exoPlayer.setMediaItem(MediaItem.fromUri(vodVideoSourceUrl), startOffsetMillis)
            }
            .disposeOnClear()

        vodPlaybackSettingsClickedSubject.toFlowable(BackpressureStrategy.LATEST)
            .switchMapSingle { selectedVideoSourceUrlFlowable.firstOrError() }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { selectedVideoSourceUrl ->
                navigationEventDispatcher.dispatch(NavigationEvent.VodPlaybackSettings(vodId, selectedVideoSourceUrl))
            }
            .disposeOnClear()
    }

    fun onVodPlaybackSettingsClicked() {
        vodPlaybackSettingsClickedSubject.onNext(Unit)
    }

    @Subscribe
    fun onVideoSourceSelected(videoSourceSelectedEvent: VideoSourceSelectedEvent) {
        userSelectedVideoSourceUrlSubject.onNext(videoSourceSelectedEvent.videoSourceUrl)
    }

    override fun onStart(owner: LifecycleOwner) {
        eventBus.register(this)
        exoPlayer.play()
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
        exoPlayer.pause()
        savedStateHandle.set(ARG_START_OFFSET_MILLIS, exoPlayer.currentPosition)
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
        private const val ARG_START_OFFSET_MILLIS = "start_offset_millis"
    }
}
