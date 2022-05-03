package com.dmko.bulldogvods.app.common.keyboard

import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun View.focusAndShowKeyboard() {
    fun View.showKeyboardNow() {
        if (isFocused) {
            post {
                val inputMethodManager = requireNotNull(context.getSystemService<InputMethodManager>())
                inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        showKeyboardNow()
    } else {
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showKeyboardNow()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            }
        )
    }
}

fun View.clearFocusAndHideKeyboard() {
    val inputMethodManager = requireNotNull(context.getSystemService<InputMethodManager>())
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    clearFocus()
}
