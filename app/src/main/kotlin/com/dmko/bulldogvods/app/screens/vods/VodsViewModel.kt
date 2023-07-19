package com.dmko.bulldogvods.app.screens.vods

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.liveData
import androidx.paging.map
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.app.screens.chapterchooser.ChapterChooserDialogEvent
import com.dmko.bulldogvods.features.vods.domain.usecases.ObserveRecentlyWatchedVodsWithPlaybackPositionUseCase
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.mapping.VodWithPlaybackPositionToVodItemMapper
import com.dmko.bulldogvods.features.vods.presentation.paging.VodsPagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@HiltViewModel
class VodsViewModel @Inject constructor(
    vodsPagerFactory: VodsPagerFactory,
    private val observeRecentlyWatchedVodsUseCase: ObserveRecentlyWatchedVodsWithPlaybackPositionUseCase,
    private val vodWithPlaybackPositionToVodItemMapper: VodWithPlaybackPositionToVodItemMapper,
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus,
    private val schedulers: Schedulers
) : RxViewModel(), DefaultLifecycleObserver {

    val vodItemsPagingDataLiveData = vodsPagerFactory.createVodsPager().liveData
        .cachedIn(viewModelScope)
        .map { pagingData ->
            pagingData
                .map(vodWithPlaybackPositionToVodItemMapper::map)
                .insertHeaderItem(item = VodItem.VodsSection(R.string.dialog_chapter_chooser_title, emptyList()))
        }

    init {
        observeRecentlyWatchedVodsUseCase.execute(false)
            .subscribeOn(schedulers.io)
            .subscribe { Log.d("dasdasd", "recently watched vods count - ${it.size}") }
            .disposeOnClear()
    }

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
