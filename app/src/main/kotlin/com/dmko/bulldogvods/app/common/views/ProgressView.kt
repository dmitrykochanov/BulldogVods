package com.dmko.bulldogvods.app.common.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.dmko.bulldogvods.R
import kotlin.math.roundToInt

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Int = 0
        set(value) {
            require(value in 0..100)
            field = value
            invalidate()
        }

    private val progressRect = Rect()
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val progressColor = ContextCompat.getColor(context, R.color.red)
    private val emptyProgressColor = ContextCompat.getColor(context, R.color.dove_gray)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val progressWidth = (width.toFloat() * progress / 100).roundToInt()
        progressRect.top = 0
        progressRect.bottom = height

        progressRect.left = 0
        progressRect.right = progressWidth
        progressPaint.color = progressColor
        canvas.drawRect(progressRect, progressPaint)

        progressRect.left = progressWidth
        progressRect.right = width
        progressPaint.color = emptyProgressColor
        canvas.drawRect(progressRect, progressPaint)
    }
}
