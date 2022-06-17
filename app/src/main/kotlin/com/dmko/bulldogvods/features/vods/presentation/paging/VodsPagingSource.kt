package com.dmko.bulldogvods.features.vods.presentation.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import com.dmko.bulldogvods.features.vods.domain.usecases.GetVodsWithPlaybackPositionUseCase
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

class VodsPagingSource(
    private val getVodsWithPlaybackPositionUseCase: GetVodsWithPlaybackPositionUseCase,
    private val searchQuery: String?
) : RxPagingSource<Int, VodWithPlaybackPosition>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, VodWithPlaybackPosition>> {
        val page = params.key ?: 0
        val limit = params.loadSize
        val forceRefresh = params is LoadParams.Refresh || searchQuery != null
        return getVodsWithPlaybackPositionUseCase.execute(page, limit, searchQuery, forceRefresh)
            .map<LoadResult<Int, VodWithPlaybackPosition>> { vods ->
                LoadResult.Page(
                    data = vods,
                    prevKey = null,
                    nextKey = if (vods.size < limit) {
                        null
                    } else {
                        page + 1
                    }
                )
            }
            .onErrorReturn { throwable ->
                Timber.e(throwable, "Failed to load vods page")
                LoadResult.Error(throwable)
            }
    }

    override fun getRefreshKey(state: PagingState<Int, VodWithPlaybackPosition>): Int? = null
}
