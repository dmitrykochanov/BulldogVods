package com.dmko.bulldogvods.features.vods.presentation.formatting

import android.text.format.DateUtils
import javax.inject.Inject

class RelativeDateFormat @Inject constructor() {

    fun format(timestamp: Long): String {
        return DateUtils.getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            DateUtils.SECOND_IN_MILLIS
        ).toString()
    }
}
