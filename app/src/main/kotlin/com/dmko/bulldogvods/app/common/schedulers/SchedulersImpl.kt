package com.dmko.bulldogvods.app.common.schedulers

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import io.reactivex.rxjava3.schedulers.Schedulers as RxSchedulers

class SchedulersImpl @Inject constructor() : Schedulers {

    override val io: Scheduler = RxSchedulers.io()

    override val ui: Scheduler = AndroidSchedulers.mainThread()
}
