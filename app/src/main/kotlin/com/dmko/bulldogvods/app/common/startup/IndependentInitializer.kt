package com.dmko.bulldogvods.app.common.startup

import androidx.startup.Initializer

abstract class IndependentInitializer : Initializer<Unit> {

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
