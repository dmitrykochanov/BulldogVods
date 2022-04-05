package com.dmko.bulldogvods.features.vods.presentation.entities

import androidx.annotation.DrawableRes

data class VodVideoSourceItem(
    val name: String,
    val url: String,
    @DrawableRes val icon: Int? = null
)
