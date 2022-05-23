package com.dmko.bulldogvods.app.common.imageloader.coil.targets

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.style.ImageSpan
import android.widget.TextView
import androidx.core.text.toSpannable
import coil.target.Target
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader.SpanPosition

class TextViewTarget(
    private val target: TextView,
    private val spanPositions: List<SpanPosition>,
    private val drawableCallback: Drawable.Callback
) : Target {

    override fun onSuccess(result: Drawable) {
        setDrawableSize(result)
        result.callback = drawableCallback
        if (result is Animatable) {
            result.start()
        }
        val spannable = target.text.toSpannable()
        spanPositions.forEach { spanIndexes ->
            spannable.setSpan(
                ImageSpan(result),
                spanIndexes.startIndexInclusive,
                spanIndexes.endIndexExclusive,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        target.text = spannable
    }

    private fun setDrawableSize(drawable: Drawable) {
        val lineHeight = target.paint.fontMetrics.bottom - target.paint.fontMetrics.top
        val aspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight
        val imageWidth = lineHeight * aspectRatio
        drawable.setBounds(0, 0, imageWidth.toInt(), lineHeight.toInt())
    }
}
