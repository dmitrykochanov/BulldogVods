package com.dmko.bulldogvods.app.common.image.loader.coil.interceptors

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.LruCache
import coil.decode.DataSource
import coil.intercept.Interceptor
import coil.request.ImageResult
import coil.request.SuccessResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AnimatedDrawableCacheInterceptor : Interceptor {

    private val cache = LruCache<String, Drawable>(100)

    private val cacheMutex = Mutex()

    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        cacheMutex.withLock {
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
                val drawable = result.drawable
                if (drawable is Animatable) {
                    cache.put(cacheKey, drawable)
                    drawable.stop()
                }
                result
            }
        }
    }
}
