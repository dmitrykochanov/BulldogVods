package com.dmko.bulldogvods.features.vods.domain.usecases

import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.VodWithPlaybackPosition
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetVodsWithPlaybackPositionUseCase @Inject constructor(
    private val networkVodsDataSource: NetworkVodsDataSource,
    private val getVodsPlaybackPositionsUseCase: GetVodsPlaybackPositionsUseCase
) {

    fun execute(
        page: Int,
        limit: Int,
        searchQuery: String?,
        forceRefresh: Boolean
    ): Single<List<VodWithPlaybackPosition>> {
        return networkVodsDataSource.getVods(page, limit, searchQuery, forceRefresh)
            .flatMap(getVodsPlaybackPositionsUseCase::execute)
    }
}
