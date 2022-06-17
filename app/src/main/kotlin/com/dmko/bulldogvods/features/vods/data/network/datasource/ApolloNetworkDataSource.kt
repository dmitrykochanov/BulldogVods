package com.dmko.bulldogvods.features.vods.data.network.datasource

import apollo.VodQuery
import apollo.VodsQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy.CacheFirst
import com.apollographql.apollo3.cache.normalized.FetchPolicy.NetworkOnly
import com.apollographql.apollo3.cache.normalized.apolloStore
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.rx3.rxSingle
import com.dmko.bulldogvods.BuildConfig
import com.dmko.bulldogvods.features.vods.data.network.mapping.VodSchemaToVodMapper
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ApolloNetworkVodsDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val vodSchemaToVodMapper: VodSchemaToVodMapper
) : NetworkVodsDataSource {

    override fun getVods(page: Int, limit: Int, searchQuery: String?, forceRefresh: Boolean): Single<List<Vod>> {
        val query = VodsQuery(
            page = page,
            limit = if (BuildConfig.DEBUG) limit else VODS_LIMIT_IN_PRODUCTION,
            searchQuery = Optional.presentIfNotNull(searchQuery)
        )
        if (forceRefresh) {
            apolloClient.apolloStore.clearAll()
        }
        return apolloClient.query(query)
            .fetchPolicy(if (forceRefresh) NetworkOnly else CacheFirst)
            .rxSingle()
            .map { apolloResponse -> requireNotNull(apolloResponse.data?.vods) }
            .map { vods -> vods.map(VodsQuery.Vod::vodSchema).map(vodSchemaToVodMapper::map) }
    }

    override fun getVod(id: String, forceRefresh: Boolean): Single<Vod> {
        return apolloClient.query(VodQuery(id))
            .fetchPolicy(if (forceRefresh) NetworkOnly else CacheFirst)
            .rxSingle()
            .map { apolloResponse -> requireNotNull(apolloResponse.data?.vod?.vodSchema) }
            .map(vodSchemaToVodMapper::map)
    }

    private companion object {

        private const val VODS_LIMIT_IN_PRODUCTION = 10
    }
}
