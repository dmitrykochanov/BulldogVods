package com.dmko.bulldogvods.app.common.resource

sealed class Resource<out DataType> {

    object Loading : Resource<Nothing>()

    data class Data<out DataType>(
        val data: DataType
    ) : Resource<DataType>()

    data class Error(
        val error: Throwable
    ) : Resource<Nothing>()
}
