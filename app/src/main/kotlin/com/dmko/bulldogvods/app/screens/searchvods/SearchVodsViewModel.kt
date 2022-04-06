package com.dmko.bulldogvods.app.screens.searchvods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventDispatcher
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.mapping.VodToVodItemMapper
import com.dmko.bulldogvods.features.vods.presentation.paging.VodsPagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class SearchVodsViewModel @Inject constructor(
    private val vodsPagerFactory: VodsPagerFactory,
    private val vodToVodItemMapper: VodToVodItemMapper,
    private val navigationEventDispatcher: NavigationEventDispatcher,
    schedulers: Schedulers,
    private val savedStateHandle: SavedStateHandle
) : RxViewModel() {

    private val searchClickedSubject = PublishSubject.create<Unit>()

    private val vodsPagingDataMutableLiveData = MutableLiveData<PagingData<VodItem>>()
    val vodsPagingDataLiveData: LiveData<PagingData<VodItem>> = vodsPagingDataMutableLiveData.cachedIn(viewModelScope)

    init {
        searchClickedSubject.toFlowable(BackpressureStrategy.LATEST)
            .startWithItem(Unit)
            .map { savedStateHandle.get<String>(ARG_SEARCH_QUERY).orEmpty() }
            .switchMap { searchQuery -> vodsPagerFactory.createVodsPager(searchQuery).flowable }
            .map { pagingData -> pagingData.map(vodToVodItemMapper::map) }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(vodsPagingDataMutableLiveData::setValue)
            .disposeOnClear()
    }

    fun onSearchQueryChanged(query: String) {
        savedStateHandle[ARG_SEARCH_QUERY] = query.trim()
    }

    fun onSearchClicked() {
        searchClickedSubject.onNext(Unit)
    }

    fun onVodClicked(vodId: String) {
        navigationEventDispatcher.dispatch(NavigationEvent.VodPlayback(vodId))
    }

    fun onVodChaptersClicked(vodId: String) {
        navigationEventDispatcher.dispatch(NavigationEvent.ChapterChooser(vodId))
    }

    private companion object {

        private const val ARG_SEARCH_QUERY = "search_query"
    }
}