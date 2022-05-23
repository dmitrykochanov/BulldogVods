package com.dmko.bulldogvods.app.common.imageloader

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.dispose
import coil.load
import coil.request.Disposable
import coil.request.ImageRequest
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition
import com.dmko.bulldogvods.app.common.imageloader.coil.targets.TextViewTarget
import javax.inject.Inject

class CoilImageLoader @Inject constructor(
    private val imageLoader: coil.ImageLoader
) : ImageLoader {

    private val drawableCallbacksMap = hashMapOf<String, DrawableMultiCallback>()

    override fun load(url: String, target: ImageView) {
        target.load(url, imageLoader)
    }

    override fun loadIntoSpans(url: String, spanPositions: List<SpanPosition>, target: TextView) {
        val request = ImageRequest.Builder(target.context)
            .data(url)
            .target(
                TextViewTarget(
                    target = target,
                    spanPositions = spanPositions,
                    drawableCallback = drawableCallbacksMap
                        .getOrPut(url) { DrawableMultiCallback(true) }
                        .also { it.addView(target) }
                )
            )
            .build()
        val disposable = imageLoader.enqueue(request)
        saveTextViewDisposable(disposable, target)
    }

    @Suppress("UNCHECKED_CAST")
    private fun saveTextViewDisposable(disposable: Disposable, target: TextView) {
        val savedDisposables = target.getTag(R.id.coil_disposables_list) as? List<Disposable>
        val newDisposables = if (savedDisposables == null) {
            listOf(disposable)
        } else {
            savedDisposables + disposable
        }
        target.setTag(R.id.coil_disposables_list, newDisposables)
    }

    override fun dispose(target: View) {
        when (target) {
            is ImageView -> target.dispose()
            is TextView -> target.dispose()
            else -> throw IllegalArgumentException("Can't dispose target $target")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun TextView.dispose() {
        (getTag(R.id.coil_disposables_list) as? List<Disposable>)?.forEach(Disposable::dispose)
        setTag(R.id.coil_disposables_list, null)
        drawableCallbacksMap.values.forEach { callback -> callback.removeView(this) }
    }
}
