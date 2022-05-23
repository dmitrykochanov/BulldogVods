package com.dmko.bulldogvods.app.common.imageloader

import android.graphics.drawable.Drawable
import android.view.View
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class DrawableMultiCallback(
    private val useViewInvalidate: Boolean = false
) : Drawable.Callback {

    private val callbacks = CopyOnWriteArrayList<CallbackWeakReference>()

    override fun invalidateDrawable(drawable: Drawable) {
        for (reference in callbacks) {
            val callback = reference.get()
            if (callback != null) {
                if (useViewInvalidate && callback is View) {
                    callback.invalidate()
                } else {
                    callback.invalidateDrawable(drawable)
                }
            } else {
                callbacks.remove(reference)
            }
        }
    }

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, timestamp: Long) {
        for (reference in callbacks) {
            val callback = reference.get()
            callback?.scheduleDrawable(drawable, runnable, timestamp) ?: callbacks.remove(reference)
        }
    }

    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) {
        for (reference in callbacks) {
            val callback = reference.get()
            callback?.unscheduleDrawable(drawable, runnable) ?: callbacks.remove(reference)
        }
    }

    fun addView(callback: Drawable.Callback) {
        for (reference in callbacks) {
            val item = reference.get()
            if (item == null) {
                callbacks.remove(reference)
            }
        }
        callbacks.addIfAbsent(CallbackWeakReference(callback))
    }

    fun removeView(callback: Drawable.Callback) {
        for (reference in callbacks) {
            val item = reference.get()
            if (item == null || item === callback) {
                callbacks.remove(reference)
            }
        }
    }

    internal class CallbackWeakReference(callback: Drawable.Callback) : WeakReference<Drawable.Callback>(callback) {

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            return if (other == null || javaClass != other.javaClass) {
                false
            } else {
                get() === (other as CallbackWeakReference).get()
            }
        }

        override fun hashCode(): Int {
            val callback = get()
            return callback?.hashCode() ?: 0
        }
    }
}
