package com.dmko.bulldogvods.app.common.image.loader

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import coil.dispose
import coil.load
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CoilImageLoader @Inject constructor(
    private val imageLoader: coil.ImageLoader,
    @ApplicationContext private val context: Context
) : ImageLoader {

    override fun load(url: String, target: ImageView) {
        target.load(url, imageLoader)
    }

    override fun load(url: String): Single<Drawable> {
        return Single.create { emitter ->
            val request = ImageRequest.Builder(context)
                .data(url)
                .target(
                    onSuccess = { drawable -> emitter.onSuccess(drawable) },
                    onError = { emitter.onError(Exception("Failed to load drawable for url $url")) }
                )
                .build()
            val disposable = imageLoader.enqueue(request)
            emitter.setCancellable { disposable.dispose() }
        }
    }

    override fun dispose(target: ImageView) {
        target.dispose()
    }
}
