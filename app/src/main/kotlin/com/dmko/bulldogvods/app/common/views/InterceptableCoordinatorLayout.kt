package com.dmko.bulldogvods.app.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

class InterceptableCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

    fun interface OnTouchEventInterceptedListener {

        fun onTouchEventIntercepted(event: MotionEvent)
    }

    var onTouchEventInterceptedListener: OnTouchEventInterceptedListener? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        onTouchEventInterceptedListener?.onTouchEventIntercepted(event)
        return super.onInterceptTouchEvent(event)
    }
}
