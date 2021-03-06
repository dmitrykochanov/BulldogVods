package com.dmko.bulldogvods.features.theming.data.device

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatDelegate
import com.dmko.bulldogvods.features.theming.data.mapping.ThemeToAndroidNightModeMapper
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import timber.log.Timber
import javax.inject.Inject

class AndroidDeviceThemeManager @Inject constructor(
    private val themeToAndroidNightModeMapper: ThemeToAndroidNightModeMapper
) : DeviceThemeManager {

    @MainThread
    override fun changeDeviceTheme(theme: Theme) {
        Timber.i("Changing device theme to $theme")
        val nightMode = themeToAndroidNightModeMapper.map(theme)
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}
