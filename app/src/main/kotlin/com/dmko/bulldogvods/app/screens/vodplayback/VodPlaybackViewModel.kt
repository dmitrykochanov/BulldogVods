package com.dmko.bulldogvods.app.screens.vodplayback

import android.content.Context
import androidx.lifecycle.*
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.domain.entities.VodVideoSource
import com.dmko.bulldogvods.features.vods.domain.selector.DefaultVodVideoSourceSelector
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class VodPlaybackViewModel @Inject constructor(
    networkVodsDataSource: NetworkVodsDataSource,
    defaultVodVideoSourceSelector: DefaultVodVideoSourceSelector,
    schedulers: Schedulers,
    @ApplicationContext context: Context,
    private val savedStateHandle: SavedStateHandle
) : RxViewModel(), DefaultLifecycleObserver {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val startOffsetMillis = requireNotNull(savedStateHandle.get<Long>(ARG_START_OFFSET_MILLIS))

    private val exoPlayer = ExoPlayer.Builder(context.applicationContext).build()

    private val selectVideoSourceClickedSubject = PublishSubject.create<Unit>()
    private val userSelectedVideoSourceSubject = PublishSubject.create<VodVideoSource>()

    private val playerMutableLiveData = MutableLiveData<Player>()
    val playerLiveData: LiveData<Player> = playerMutableLiveData

    init {
        playerMutableLiveData.value = exoPlayer
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        val readyVideoSourcesSingle = networkVodsDataSource.getVod(vodId)
            .map(Vod::videoSources)
            .map { videoSources -> videoSources.filter { it.isReady } }

        val defaultVideoSourceSingle = readyVideoSourcesSingle
            .map(defaultVodVideoSourceSelector::selectDefaultVideoSource)

        val userSelectedVideoSourceFlowable = userSelectedVideoSourceSubject
            .toFlowable(BackpressureStrategy.DROP)

        userSelectedVideoSourceFlowable.mergeWith(defaultVideoSourceSingle)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { vodVideoSource ->
                exoPlayer.setMediaItem(MediaItem.fromUri(vodVideoSource.url), startOffsetMillis)
            }
            .disposeOnClear()

        selectVideoSourceClickedSubject.toFlowable(BackpressureStrategy.DROP)
            .switchMapSingle { readyVideoSourcesSingle }
    }

    fun onSelectVideoSourceClicked() {

    }

    fun onVideoSourceSelected(selectedSource: VodVideoSource) {

    }

    override fun onStart(owner: LifecycleOwner) {
        exoPlayer.play()
    }

    override fun onStop(owner: LifecycleOwner) {
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
