package com.dmko.bulldogvods.app.common.imageloader

import android.widget.ImageView

interface ImageLoader {

    fun load(url: String, target: ImageView)

    fun clear(target: ImageView)
}
