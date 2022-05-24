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
        when (resource) {
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

fun <T : Any, R : Any> Flowable<Resource<T>>.mapResource(transform: (T) -> R): Flowable<Resource<R>> {
    return map { resource ->
        when (resource) {
            is Resource.Loading -> resource
            is Resource.Data -> Resource.Data(transform(resource.data))
            is Resource.Error -> resource
        }
    }
}

fun <T : Any, R : Any> Flowable<Resource<T>>.switchMapResource(
    transform: (T) -> Flowable<Resource<R>>
): Flowable<Resource<R>> {
    return switchMap { resource ->
        when (resource) {
            is Resource.Loading -> Flowable.just(resource)
            is Resource.Data -> transform(resource.data)
            is Resource.Error -> Flowable.just(resource)
        }
    }
}
