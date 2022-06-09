package com.dmko.bulldogvods.app.common.image.loader.coil.interceptors

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.LruCache
import coil.decode.DataSource
import coil.drawable.MovieDrawable
import coil.drawable.ScaleDrawable
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.request.SuccessResult
import com.github.penfeizhou.animation.webp.WebPDrawable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AnimatedDrawableCacheInterceptor : Interceptor {

    private val cache = LruCache<String, Drawable>(100)

    private val mutex = Mutex()

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        mutex.withLock {
            val cacheKey = chain.request.data.toString()
            val cachedDrawable = cache.get(cacheKey)
            return if (cachedDrawable != null) {
                SuccessResult(
                    drawable = cachedDrawable,
                    request = chain.request,
                    dataSource = DataSource.MEMORY_CACHE
                )
            } else {
                val result = chain.proceed(chain.request)
                if (result is SuccessResult && isAnimatedDrawable(result.drawable)) {
                    cache.put(cacheKey, result.drawable)
                }
                result
            }
        }
    }

    private fun isAnimatedDrawable(drawable: Drawable): Boolean {
        return drawable is MovieDrawable || drawable is WebPDrawable ||
                SDK_INT >= VERSION_CODES.P && (drawable is AnimatedImageDrawable ||
                drawable is ScaleDrawable && drawable.child is AnimatedImageDrawable)
    }
}
