package com.dmko.bulldogvods.features.vods.data.database.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface DatabaseVodsDataSource {

    fun getVodPlaybackPosition(vodId: String): Flowable<Long>

    fun saveVodPlaybackPosition(vodId: String, playbackPosition: Long): Completable
}
