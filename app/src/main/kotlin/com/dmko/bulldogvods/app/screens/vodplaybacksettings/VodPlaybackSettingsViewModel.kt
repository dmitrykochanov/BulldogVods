package com.dmko.bulldogvods.app.screens.vodplaybacksettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.app.common.resource.asResource
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.VideoSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.presentation.entities.VideoSourceItem
import com.dmko.bulldogvods.features.vods.presentation.events.VideoSourceSelectedEvent
import com.dmko.bulldogvods.features.vods.presentation.mapping.VideoSourceToVideoSourceItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class VodPlaybackSettingsViewModel @Inject constructor(
    private val videoSourceToVideoSourceItemMapper: VideoSourceToVideoSourceItemMapper,
    private val navigationEventDispatcher: NavigationEventDispatcher,
    private val eventBus: EventBus,
    vodsDataSource: NetworkVodsDataSource,
    schedulers: Schedulers,
    savedStateHandle: SavedStateHandle
) : RxViewModel() {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val selectedVideoSourceUrl = requireNotNull(savedStateHandle.get<String>(ARG_SELECTED_VIDEO_SOURCE_URL))

    private val loadVodSubject = PublishSubject.create<Unit>()

    private val vodVideoSourceItemsMutableLiveData = MutableLiveData<Resource<List<VideoSourceItem>>>()
    val videoSourceItemsLiveData: LiveData<Resource<List<VideoSourceItem>>> = vodVideoSourceItemsMutableLiveData

    init {
        loadVodSubject.toFlowable(BackpressureStrategy.LATEST)
            .startWithItem(Unit)
            .switchMap {
                vodsDataSource.getVod(vodId)
                    .map(::createVideoSourceItems)
                    .asResource()
            }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(vodVideoSourceItemsMutableLiveData::setValue)
            .disposeOnClear()
    }

    private fun createVideoSourceItems(vod: Vod): List<VideoSourceItem> {
        return vod.videoSources
            .filter(VideoSource::isReady)
            .map { videoSource -> videoSourceToVideoSourceItemMapper.map(selectedVideoSourceUrl, videoSource) }
    }

    fun onVideoSourceClicked(videoSourceUrl: String) {
        eventBus.post(VideoSourceSelectedEvent(videoSourceUrl))
        navigationEventDispatcher.dispatch(NavigationEvent.Back)
    }

    fun onRetryClicked() {
        loadVodSubject.onNext(Unit)
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
        private const val ARG_SELECTED_VIDEO_SOURCE_URL = "selected_video_source_url"
    }
}
