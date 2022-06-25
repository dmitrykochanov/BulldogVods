package com.dmko.bulldogvods.app.common.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.dmko.bulldogvods.app.AppActivity
import com.google.android.material.slider.Slider

fun Fragment.requireAppActivity(): AppActivity {
    return requireActivity() as AppActivity
}

@ColorInt
fun Context.resolveColor(@AttrRes colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

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

fun Slider.setOnStopTrackingTouchListener(listener: (Float) -> Unit) {
    addOnSliderTouchListener(
        object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
            }

            override fun onStopTrackingTouch(slider: Slider) {
                listener(slider.value)
            }
        }
    )
}
