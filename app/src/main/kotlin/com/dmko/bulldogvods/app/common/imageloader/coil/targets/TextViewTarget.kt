package com.dmko.bulldogvods.app.common.imageloader.coil.targets

import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.core.graphics.drawable.updateBounds
import androidx.core.text.toSpannable
import coil.target.Target
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition

class TextViewTarget(
    private val target: TextView,
    private val spanPositions: List<SpanPosition>,
    private val drawableCallback: Drawable.Callback
) : Target {

    override fun onSuccess(result: Drawable) {
        result.callback = drawableCallback
        if (result is Animatable) {
            result.start()
        }
        val spannable = target.text.toSpannable()
        spanPositions.forEach { spanPosition ->
            setDrawableSizeIfNeeded(result, spanPosition.shouldOverlapWithPrevious)
            spannable.setSpan(
                ImageSpan(result),
                spanPosition.startIndexInclusive,
                spanPosition.endIndexExclusive,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        target.text = spannable
    }

    private fun setDrawableSizeIfNeeded(drawable: Drawable, shouldOverlapWithPrevious: Boolean) {
        if (drawable.isBoundsInitialized()) return
        val lineHeight = target.paint.fontMetrics.bottom - target.paint.fontMetrics.top
        val drawableAspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight
        val calculatedDrawableWidth = (lineHeight * drawableAspectRatio).toInt()
        val overlapOffset = if (shouldOverlapWithPrevious) {
            calculatedDrawableWidth + target.paint.measureText(" ").toInt()
        } else {
            0
        }
        drawable.updateBounds(
            left = 0 - overlapOffset,
            top = 0,
            right = calculatedDrawableWidth - overlapOffset,
            bottom = lineHeight.toInt()
        )
    }

    private fun Drawable.isBoundsInitialized(): Boolean {
        return bounds != ZERO_BOUNDS_RECT
    }

    private companion object {

        private val ZERO_BOUNDS_RECT = Rect()
    }
}
