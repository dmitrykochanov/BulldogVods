package com.dmko.bulldogvods.app.common.schedulers

import io.reactivex.rxjava3.core.Scheduler

interface Schedulers {

    val io: Scheduler

    val ui: Scheduler
}
