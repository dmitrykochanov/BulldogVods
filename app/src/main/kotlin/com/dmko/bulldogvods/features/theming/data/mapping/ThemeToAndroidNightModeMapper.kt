package com.dmko.bulldogvods.features.theming.data.mapping

import androidx.appcompat.app.AppCompatDelegate
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import javax.inject.Inject

class ThemeToAndroidNightModeMapper @Inject constructor() {

    fun map(source: Theme): Int {
        return when (source) {
            Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Theme.SYSTEM_DEFAULT -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
}
