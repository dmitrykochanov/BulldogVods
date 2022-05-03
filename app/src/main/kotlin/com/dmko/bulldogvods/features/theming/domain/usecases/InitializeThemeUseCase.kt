package com.dmko.bulldogvods.features.theming.domain.usecases

import com.dmko.bulldogvods.features.theming.data.device.DeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.local.LocalThemeDataSource
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class InitializeThemeUseCase @Inject constructor(
    private val localThemeDataSource: LocalThemeDataSource,
    private val deviceThemeManager: DeviceThemeManager
) {

    fun execute(): Completable {
        return localThemeDataSource.themeFlowable
            .firstOrError()
            .flatMapCompletable { themeOptional ->
                if (themeOptional.isPresent) {
                    Completable.fromAction { deviceThemeManager.changeDeviceTheme(themeOptional.get()) }
                } else {
                    localThemeDataSource.saveTheme(Theme.SYSTEM_DEFAULT)
                }
            }
    }
}
