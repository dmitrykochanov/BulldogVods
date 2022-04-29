package com.dmko.bulldogvods.app.common.resource

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

fun <DataType : Any> Single<DataType>.asResource(): Flowable<Resource<DataType>> {
    return map<Resource<DataType>> { data -> Resource.Data(data) }
        .onErrorReturn { throwable -> Resource.Error(throwable) }
        .toFlowable()
        .startWithItem(Resource.Loading)
}
