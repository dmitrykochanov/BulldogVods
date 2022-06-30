package com.dmko.bulldogvods.app.common.startup

import android.content.Context
import androidx.startup.Initializer
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import timber.log.Timber

class RxJavaInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        RxJavaPlugins.setErrorHandler { error ->
            Timber.e(error, "Uncaught RxJava exception")
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
