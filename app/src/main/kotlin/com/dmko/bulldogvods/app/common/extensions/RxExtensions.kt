package com.dmko.bulldogvods.app.common.extensions

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.rxjava3.RxDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.rx3.asFlowable

@OptIn(ExperimentalCoroutinesApi::class)
fun RxDataStore<Preferences>.updateData(transform: (MutablePreferences) -> Unit): Completable {
    return updateDataAsync { prefs ->
        val mutablePrefs = prefs.toMutablePreferences()
        transform(mutablePrefs)
        Single.just(mutablePrefs)
    }
        .ignoreElement()
}

fun <T : Any> SavedStateHandle.getFlowable(key: String): Flowable<T> {
    return getLiveData<T>(key).asFlow().asFlowable()
}
