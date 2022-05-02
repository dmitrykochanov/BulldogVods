package com.dmko.bulldogvods.app.screens.vods

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.paging.map
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.app.screens.chapterchooser.ChapterChooserDialogEvent
import com.dmko.bulldogvods.features.vods.presentation.mapping.VodToVodItemMapper
import com.dmko.bulldogvods.features.vods.presentation.paging.VodsPagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class VodsViewModel @Inject constructor(
    vodsPagerFactory: VodsPagerFactory,
    private val vodToVodItemMapper: VodToVodItemMapper,
    private val navigationEventDispatcher: NavigationEventDispatcher,
    private val eventBus: EventBus
) : RxViewModel(), DefaultLifecycleObserver {

    val vodItemsPagingDataLiveData = vodsPagerFactory.createVodsPager().liveData
        .cachedIn(viewModelScope)
        .map { pagingData -> pagingData.map(vodToVodItemMapper::map) }

    fun onSearchClicked() {
        navigationEventDispatcher.dispatch(NavigationEvent.SearchVods)
    }

    fun onVodClicked(vodId: String) {
        navigationEventDispatcher.dispatch(NavigationEvent.VodPlayback(vodId))
    }

    fun onVodChaptersClicked(vodId: String) {
        navigationEventDispatcher.dispatch(NavigationEvent.ChapterChooser(vodId))
    }

    @Subscribe
    fun onChapterChooserDialogEvent(event: ChapterChooserDialogEvent) {
        navigationEventDispatcher.dispatch(NavigationEvent.VodPlayback(event.vodId, event.selectedStartOffset))
    }

    override fun onStart(owner: LifecycleOwner) {
        eventBus.register(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
    }
}
