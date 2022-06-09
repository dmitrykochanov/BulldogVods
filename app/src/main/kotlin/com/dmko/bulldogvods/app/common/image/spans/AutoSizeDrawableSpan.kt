package com.dmko.bulldogvods.app.common.image.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.core.graphics.drawable.updateBounds

class AutoSizeDrawableSpan(
    val drawable: Drawable,
    private val isZeroWidth: Boolean = false
) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        if (fm != null) {
            setDrawableBoundsIfNeeded(fm)
        }
        return if (isZeroWidth) {
            0
        } else {
            drawable.bounds.right
        }
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        val translationX = if (isZeroWidth) {
            x - drawable.bounds.right - paint.measureText(" ")
        } else {
            x
        }
        canvas.translate(translationX, top.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }

    private fun setDrawableBoundsIfNeeded(fontMetrics: Paint.FontMetricsInt) {
        if (!drawable.isBoundsInitialized()) {
            val lineHeight = (fontMetrics.bottom - fontMetrics.top)
            val drawableAspectRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight
            val calculatedDrawableWidth = (lineHeight * drawableAspectRatio).toInt()
            drawable.updateBounds(
                left = 0,
                top = 0,
                right = calculatedDrawableWidth,
                bottom = lineHeight
            )
        }
    }

    private fun Drawable.isBoundsInitialized(): Boolean {
        return bounds.left != 0 || bounds.right != 0 || bounds.top != 0 || bounds.bottom != 0
    }
}
