package com.dmko.bulldogvods.app.common.imageloader

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.dmko.bulldogvods.app.common.imageloader.coil.decoders.AnimatedWebPDecoder
import com.dmko.bulldogvods.app.common.imageloader.coil.interceptors.AnimatedDrawableCacheInterceptor
import com.dmko.bulldogvods.app.common.imageloader.coil.logger.CoilTimberLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CoilImageLoaderFactory @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createCoilImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .logger(CoilTimberLogger())
            .components {
                if (SDK_INT >= VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                    add(AnimatedWebPDecoder.Factory())
                }
                add(AnimatedDrawableCacheInterceptor())
            }
            .build()
    }
}
