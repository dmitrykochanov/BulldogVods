package com.dmko.bulldogvods.features.vods.data.network.datasource

import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.Single

interface NetworkVodsDataSource {

    fun getVods(page: Int, limit: Int): Single<List<Vod>>

    fun getVod(id: String): Single<Vod>
}
