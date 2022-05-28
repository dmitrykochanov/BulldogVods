package com.dmko.bulldogvods.app.screens.chapterchooser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.resource.Resource
import com.dmko.bulldogvods.app.common.resource.asResource
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.presentation.entities.ChapterItem
import com.dmko.bulldogvods.features.vods.presentation.mapping.VodChapterToChapterItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class ChapterChooserViewModel @Inject constructor(
    private val vodChapterToChapterItemMapper: VodChapterToChapterItemMapper,
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val eventBus: EventBus,
    networkVodsDataSource: NetworkVodsDataSource,
    schedulers: Schedulers,
    savedStateHandle: SavedStateHandle
) : RxViewModel() {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))

    private val loadVodsSubject = PublishSubject.create<Unit>()

    private val chapterItemsMutableLiveData = MutableLiveData<Resource<List<ChapterItem>>>()
    val chapterItemsLiveData: LiveData<Resource<List<ChapterItem>>> = chapterItemsMutableLiveData

    init {
        loadVodsSubject.toFlowable(BackpressureStrategy.DROP)
            .startWithItem(Unit)
            .switchMap {
                networkVodsDataSource.getVod(vodId)
                    .map { vod -> vod.chapters.map(vodChapterToChapterItemMapper::map) }
                    .asResource()
            }
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chapterItemsMutableLiveData::setValue)
            .disposeOnClear()
    }

    fun onChapterClicked(startOffset: Duration) {
        navigationCommandDispatcher.dispatch(NavigationCommand.Back)
        eventBus.post(ChapterChooserDialogEvent(vodId, startOffset))
    }

    fun onRetryClicked() {
        loadVodsSubject.onNext(Unit)
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
    }
}
