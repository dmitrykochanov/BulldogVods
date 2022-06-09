package com.dmko.bulldogvods.app.common.image.loader

import android.graphics.drawable.Drawable
import android.widget.ImageView
import io.reactivex.rxjava3.core.Single

interface ImageLoader {

    fun load(url: String, target: ImageView)

    fun load(url: String): Single<Drawable>

    fun dispose(target: ImageView)
}
