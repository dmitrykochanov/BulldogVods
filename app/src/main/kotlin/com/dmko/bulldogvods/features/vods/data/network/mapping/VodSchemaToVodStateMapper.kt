package com.dmko.bulldogvods.features.vods.data.network.mapping

import apollo.fragment.VodSchema
import com.dmko.bulldogvods.features.vods.domain.entities.VodState
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import apollo.type.VodState as ApolloVodState

class VodSchemaToVodStateMapper @Inject constructor(
    private val dateStringToTimestampMapper: DateStringToTimestampMapper
) {

    fun map(vodSchema: VodSchema): VodState {
        val vodStartedAtMillis = dateStringToTimestampMapper.map(vodSchema.started_at as String)
        val vodEndedAtMillis = (vodSchema.ended_at as String?)?.let(dateStringToTimestampMapper::map)
        return when (vodSchema.state) {
            ApolloVodState.Live -> VodState.Live
            ApolloVodState.Ready -> {
                requireNotNull(vodEndedAtMillis)
                VodState.Ready(
                    length = (vodEndedAtMillis - vodStartedAtMillis).milliseconds
                )
            }
            ApolloVodState.Processing -> {
                requireNotNull(vodEndedAtMillis)
                VodState.Processing(
                    length = (vodEndedAtMillis - vodStartedAtMillis).milliseconds
                )
            }
            else -> VodState.Unknown
        }
    }
}