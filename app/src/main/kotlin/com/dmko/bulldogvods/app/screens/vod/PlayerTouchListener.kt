package com.dmko.bulldogvods.app.screens.vod

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat

class PlayerTouchListener(
    context: Context,
    val onSingleClick: () -> Unit = {},
    val onDoubleClick: () -> Unit = {},
    val onScaleUp: () -> Unit = {},
    val onScaleDown: () -> Unit = {}
) : View.OnTouchListener {

    private val gestureDetector = GestureDetectorCompat(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent) = true

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onSingleClick()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleClick()
                return true
            }
        }
    )

    private val scaleGestureDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.OnScaleGestureListener {

            private var startSpan: Float = 0f

            override fun onScale(detector: ScaleGestureDetector) = false

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                startSpan = detector.currentSpan
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                val scaleDelta = detector.currentSpan - startSpan
                if (scaleDelta > 0) {
                    onScaleUp()
                } else {
                    onScaleDown()
                }
            }
        }
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, e: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(e) || scaleGestureDetector.onTouchEvent(e) || view.onTouchEvent(e)
    }
}
