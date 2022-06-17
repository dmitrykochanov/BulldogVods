package com.dmko.bulldogvods.app.common.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
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
        val memoryCacheFactory = MemoryCacheFactory(maxSizeBytes = MEMORY_CACHE_SIZE_BYTES)
        val databaseCacheFactory = SqlNormalizedCacheFactory(DATABASE_CACHE_NAME)
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(loggingOkHttpClient)
            .normalizedCache(memoryCacheFactory.chain(databaseCacheFactory), writeToCacheAsynchronously = true)
            .build()
    }

    private companion object {

        private const val BASE_URL = "https://vods.admiralbulldog.live/api/gql"
        private const val MEMORY_CACHE_SIZE_BYTES = 10 * 1024 * 1024
        private const val DATABASE_CACHE_NAME = "bulldog_vods_apollo_cache.db"
    }
}
