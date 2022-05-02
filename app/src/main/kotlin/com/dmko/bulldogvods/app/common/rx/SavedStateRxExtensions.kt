package com.dmko.bulldogvods.app.common.rx

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.rx3.asFlowable

fun <T : Any> SavedStateHandle.getFlowable(key: String): Flowable<T> {
    return getLiveData<T>(key).asFlow().asFlowable()
}
