package com.dmko.bulldogvods.features.theming.data.local

import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.util.Optional

interface LocalThemeDataSource {

    val themeFlowable: Flowable<Optional<Theme>>

    fun saveTheme(theme: Theme): Completable
}
