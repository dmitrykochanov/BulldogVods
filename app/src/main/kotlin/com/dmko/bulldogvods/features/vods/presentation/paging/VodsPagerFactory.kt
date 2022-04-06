package com.dmko.bulldogvods.features.vods.presentation.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import javax.inject.Inject

class VodsPagerFactory @Inject constructor(
    private val networkVodsDataSource: NetworkVodsDataSource
) {

    fun createVodsPager(searchQuery: String? = null): Pager<Int, Vod> {
        return Pager(
            config = PagingConfig(pageSize = VODS_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { VodsPagingSource(networkVodsDataSource, searchQuery) }
        )
    }

    private companion object {

        private const val VODS_PAGE_SIZE = 20
    }
}