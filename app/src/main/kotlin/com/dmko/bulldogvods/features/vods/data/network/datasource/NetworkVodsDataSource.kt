package com.dmko.bulldogvods.features.vods.data.network.datasource

import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.Single

interface NetworkVodsDataSource {

    fun getVods(page: Int, limit: Int, searchQuery: String?, forceRefresh: Boolean = false): Single<List<Vod>>

    fun getVod(id: String, forceRefresh: Boolean = false): Single<Vod>
}
