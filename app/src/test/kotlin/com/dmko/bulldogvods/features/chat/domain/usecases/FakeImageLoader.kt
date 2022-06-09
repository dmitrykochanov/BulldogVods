package com.dmko.bulldogvods.features.chat.domain.usecases

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import io.reactivex.rxjava3.core.Single

class FakeImageLoader : ImageLoader {
    override fun load(url: String, target: ImageView) {
    }

    override fun load(url: String): Single<Drawable> {
        return Single.error(Exception())
    }

    override fun dispose(target: ImageView) {
    }
}
