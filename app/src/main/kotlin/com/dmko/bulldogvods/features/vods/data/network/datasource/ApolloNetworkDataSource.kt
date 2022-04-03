package com.dmko.bulldogvods.features.vods.data.network.datasource

import apollo.VodsQuery
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.rx3.rxSingle
import com.dmko.bulldogvods.features.vods.data.network.mapping.VodSchemaToVodMapper
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ApolloNetworkVodsDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val vodSchemaToVodMapper: VodSchemaToVodMapper
) : NetworkVodsDataSource {


    override fun getVods(page: Int, limit: Int): Single<List<Vod>> {
        return apolloClient
            .query(VodsQuery(BULLDOG_USER_ID, page, limit))
            .rxSingle()
            .map { apolloResponse -> requireNotNull(apolloResponse.data?.vodSchemas) }
            .map { vods -> vods.map(vodSchemaToVodMapper::map) }
    }

    private companion object {

        private const val BULLDOG_USER_ID = "61e33d2940bb32eb56745580"
    }
}
