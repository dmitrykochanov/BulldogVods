package com.dmko.bulldogvods.app.common.resource

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

fun <T : Any> Single<T>.asResource(): Flowable<Resource<T>> {
    return map<Resource<T>> { data -> Resource.Data(data) }
        .onErrorReturn { throwable -> Resource.Error(throwable) }
        .toFlowable()
        .startWithItem(Resource.Loading)
}

fun <T : Any> Flowable<T>.asResource(): Flowable<Resource<T>> {
    return map<Resource<T>> { data -> Resource.Data(data) }
        .onErrorReturn { throwable -> Resource.Error(throwable) }
        .startWithItem(Resource.Loading)
}

fun <T : Any> Flowable<Resource<T>>.unwrapResource(
    propagateError: Boolean = false,
    defaultValue: T? = null
): Flowable<T> {
    return switchMap { resource ->
        return@switchMap when (resource) {
            is Resource.Loading -> Flowable.empty()
            is Resource.Data -> Flowable.just(resource.data)
            is Resource.Error -> {
                when {
                    propagateError -> Flowable.error(resource.error)
                    defaultValue != null -> Flowable.just(defaultValue)
                    else -> Flowable.empty()
                }
            }
        }
    }
}