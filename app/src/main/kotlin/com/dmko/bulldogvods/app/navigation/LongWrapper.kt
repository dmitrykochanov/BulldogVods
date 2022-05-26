package com.dmko.bulldogvods.app.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LongWrapper(
    val value: Long
) : Parcelable
