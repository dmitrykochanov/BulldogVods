package com.dmko.bulldogvods.features.vods.data.database.datasource

import com.dmko.bulldogvods.features.vods.domain.entities.VodPlaybackPosition
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface DatabaseVodsDataSource {

    fun observeVodPlaybackPosition(vodId: String): Flowable<Long>

    fun getVodPlaybackPositions(vodIds: List<String>): Single<List<VodPlaybackPosition>>

    fun saveVodPlaybackPosition(vodId: String, playbackPosition: Long): Completable
}
