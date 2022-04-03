package com.dmko.bulldogvods.features.vods.presentation.paging

import androidx.paging.PagingState
import androidx.paging.rxjava3.RxPagingSource
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

class VodsPagingSource(
    private val networkVodsDataSource: NetworkVodsDataSource
) : RxPagingSource<Int, Vod>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Vod>> {
        val page = params.key ?: 0
        val limit = params.loadSize
        return networkVodsDataSource.getVods(page, limit)
            .map<LoadResult<Int, Vod>> { vods ->
                Timber.d(vods.toString())
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

    override fun getRefreshKey(state: PagingState<Int, Vod>): Int? = null
}
