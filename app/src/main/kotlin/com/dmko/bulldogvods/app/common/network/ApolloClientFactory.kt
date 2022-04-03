package com.dmko.bulldogvods.app.common.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import javax.inject.Inject

class ApolloClientFactory @Inject constructor() {

    fun createApolloClient(): ApolloClient {
        val loggingOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { setLevel(Level.BODY) })
            .build()
        return ApolloClient.Builder()
            .serverUrl(BASE_URL)
            .okHttpClient(loggingOkHttpClient)
            .build()
    }

    private companion object {

        private const val BASE_URL = "https://vods.admiralbulldog.live/api/gql"
    }
}
