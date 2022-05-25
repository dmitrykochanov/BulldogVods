package com.dmko.bulldogvods.app.common.extensions

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.toColorInt
import androidx.core.view.GestureDetectorCompat

@ColorInt
fun String.toColorIntOrNull(): Int? {
    return try {
        toColorInt()
    } catch (e: Throwable) {
        null
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.setOnDoubleClickListener(listener: () -> Unit) {
    val gestureDetector = GestureDetectorCompat(
        context,
        object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent) = true

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                performClick()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                listener()
                return true
            }
        }
    )
    setOnTouchListener { v, event ->
        if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            v.onTouchEvent(event)
        }
    }
}
