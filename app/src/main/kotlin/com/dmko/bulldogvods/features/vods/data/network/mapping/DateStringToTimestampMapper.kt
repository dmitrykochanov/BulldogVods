package com.dmko.bulldogvods.features.vods.data.network.mapping

import java.time.OffsetDateTime
import javax.inject.Inject

class DateStringToTimestampMapper @Inject constructor() {

    fun map(date: String): Long {
        return OffsetDateTime.parse(date).toInstant().toEpochMilli()
    }
}
