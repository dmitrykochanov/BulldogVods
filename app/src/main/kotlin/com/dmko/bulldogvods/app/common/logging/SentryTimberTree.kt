package com.dmko.bulldogvods.app.common.logging

import android.util.Log
import io.sentry.Sentry
import timber.log.Timber

class SentryTimberTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.VERBOSE -> logBreadcrumb(message, t)
            Log.DEBUG -> logBreadcrumb(message, t)
            Log.INFO -> logBreadcrumb(message, t)
            Log.WARN -> logBreadcrumb(message, t)
            Log.ERROR -> logEvent(message, t)
            Log.ASSERT -> logEvent(message, t)
        }
    }

    private fun logBreadcrumb(message: String, throwable: Throwable?) {
        Sentry.addBreadcrumb("$message ${throwable?.toString().orEmpty()}")
    }

    private fun logEvent(message: String, throwable: Throwable?) {
        if (throwable != null) {
            Sentry.addBreadcrumb(message)
            Sentry.captureException(throwable)
        } else {
            Sentry.captureMessage(message)
        }
    }
}
