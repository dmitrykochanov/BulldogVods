package com.dmko.bulldogvods.features.chat.data.network.mapping

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class TimestampToDateStringMapper @Inject constructor() {

    fun map(timestamp: Long): String {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC).toString()
    }
}
