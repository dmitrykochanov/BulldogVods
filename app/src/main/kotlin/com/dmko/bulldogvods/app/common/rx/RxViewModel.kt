package com.dmko.bulldogvods.app.common.rx

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign

abstract class RxViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun Disposable.disposeOnClear() {
        compositeDisposable += this
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
