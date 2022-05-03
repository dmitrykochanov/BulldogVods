package com.dmko.bulldogvods.features.theming.data.device

import androidx.annotation.MainThread
import com.dmko.bulldogvods.features.theming.domain.entities.Theme

interface DeviceThemeManager {

    @MainThread
    fun changeDeviceTheme(theme: Theme)
}
