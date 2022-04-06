package com.dmko.bulldogvods.app.common.views

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import com.dmko.bulldogvods.app.common.views.InterceptableCoordinatorLayout.OnTouchEventInterceptedListener

class OutsideTouchHelper(
    private val views: List<View>,
    private val outsideTouchAction: () -> Unit
) {

    private val viewBoundsRect = Rect()

    fun attachToCoordinatorLayout(coordinatorLayout: InterceptableCoordinatorLayout) {
        coordinatorLayout.onTouchEventInterceptedListener = OnTouchEventInterceptedListener { event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onActionDown(event.x, event.y)
            }
        }
    }

    private fun onActionDown(x: Float, y: Float) {
        views.forEach { view ->
            view.getHitRect(viewBoundsRect)
            if (viewBoundsRect.contains(x.toInt(), y.toInt())) {
                return
            }
        }
        outsideTouchAction.invoke()
    }
}
