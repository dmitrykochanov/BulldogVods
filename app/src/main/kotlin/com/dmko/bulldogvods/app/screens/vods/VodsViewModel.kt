package com.dmko.bulldogvods.app.screens.vods

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.paging.map
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
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
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus
) : RxViewModel(), DefaultLifecycleObserver {

    val vodItemsPagingDataLiveData = vodsPagerFactory.createVodsPager().liveData
        .cachedIn(viewModelScope)
        .map { pagingData -> pagingData.map(vodToVodItemMapper::map) }

    fun onSettingsClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.ThemeChooser)
    }

    fun onSearchClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.SearchVods)
    }

    fun onVodClicked(vodId: String) {
        navigationCommandDispatcher.dispatch(NavigationCommand.Vod(vodId))
    }

    fun onVodChaptersClicked(vodId: String) {
        navigationCommandDispatcher.dispatch(NavigationCommand.ChapterChooser(vodId))
    }

    @Subscribe
    fun onChapterChooserDialogEvent(event: ChapterChooserDialogEvent) {
        navigationCommandDispatcher.dispatch(NavigationCommand.Vod(event.vodId, event.selectedStartOffset))
    }

    override fun onStart(owner: LifecycleOwner) {
        eventBus.register(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        eventBus.unregister(this)
    }
}
