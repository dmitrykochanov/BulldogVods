package com.dmko.bulldogvods.app.screens.vods

import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.liveData
import androidx.paging.map
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.features.vods.presentation.mapping.VodToVodItemMapper
import com.dmko.bulldogvods.features.vods.presentation.paging.VodsPagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VodsViewModel @Inject constructor(
    vodsPagerFactory: VodsPagerFactory,
    private val vodToVodItemMapper: VodToVodItemMapper,
    private val navigationEventDispatcher: NavigationEventDispatcher
) : RxViewModel() {

    val vodItemsPagingDataLiveData = vodsPagerFactory.createVodsPager().liveData
        .cachedIn(viewModelScope)
        .map { pagingData -> pagingData.map(vodToVodItemMapper::map) }

    fun onVodClicked(vodId: String) {
        navigationEventDispatcher.dispatch(NavigationEvent.VodPlayback(vodId))
    }

    fun onVodChaptersClicked(vodId: String) {
        // TODO
    }
}
