package com.dmko.bulldogvods.app.common.network

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class OkHttpTimberLogger : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Timber.i(message)
    }
}
