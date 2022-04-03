package com.dmko.bulldogvods.app.common.imageloader

import android.widget.ImageView
import coil.clear
import coil.load
import javax.inject.Inject

class CoilImageLoader @Inject constructor() : ImageLoader {

    override fun load(url: String, target: ImageView) {
        target.load(url)
    }

    override fun clear(target: ImageView) {
        target.clear()
    }
}
