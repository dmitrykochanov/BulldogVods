package com.dmko.bulldogvods.features.vods.presentation.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import com.dmko.bulldogvods.features.vods.domain.usecases.GetVodsWithPlaybackPositionUseCase
import javax.inject.Inject

class VodsPagerFactory @Inject constructor(
    private val getVodsWithPlaybackPositionUseCase: GetVodsWithPlaybackPositionUseCase
) {

    fun createVodsPager(searchQuery: String? = null): Pager<Int, VodWithPlaybackPosition> {
        return Pager(
            config = PagingConfig(pageSize = VODS_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { VodsPagingSource(getVodsWithPlaybackPositionUseCase, searchQuery) }
        )
    }

    private companion object {

        private const val VODS_PAGE_SIZE = 20
    }
}
