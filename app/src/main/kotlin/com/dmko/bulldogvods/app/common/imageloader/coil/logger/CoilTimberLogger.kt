package com.dmko.bulldogvods.app.common.imageloader.coil.logger

import android.util.Log
import coil.util.Logger
import timber.log.Timber

class CoilTimberLogger : Logger {

    override var level = Log.VERBOSE

    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        Timber.tag(tag).log(priority, throwable, message)
    }
}
