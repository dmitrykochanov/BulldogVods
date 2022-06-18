package com.dmko.bulldogvods.app.common.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import com.dmko.bulldogvods.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import javax.inject.Inject

class ApolloClientFactory @Inject constructor() {

    fun createApolloClient(): ApolloClient {
        val loggingInterceptor = HttpLoggingInterceptor(OkHttpTimberLogger())
        loggingInterceptor.level = if (BuildConfig.DEBUG) {
            Level.BODY
        } else {
            Level.HEADERS
        }
        val loggingOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(loggingOkHttpClient)
            .normalizedCache(MemoryCacheFactory(maxSizeBytes = MEMORY_CACHE_SIZE_BYTES))
            .build()
    }

    private companion object {

        private const val BASE_URL = "https://vods.admiralbulldog.live/api/gql"
        private const val MEMORY_CACHE_SIZE_BYTES = 10 * 1024 * 1024
    }
}
