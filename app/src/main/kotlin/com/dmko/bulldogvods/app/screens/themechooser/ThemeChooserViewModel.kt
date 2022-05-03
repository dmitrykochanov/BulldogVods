package com.dmko.bulldogvods.app.screens.themechooser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.features.theming.data.device.DeviceThemeManager
import com.dmko.bulldogvods.features.theming.data.local.LocalThemeDataSource
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ThemeChooserViewModel @Inject constructor(
    private val deviceThemeManager: DeviceThemeManager,
    private val localThemeDataSource: LocalThemeDataSource,
    private val schedulers: Schedulers
) : RxViewModel() {

    private val selectedThemeMutableLiveData = MutableLiveData<Theme>()
    val selectedThemeLiveData: LiveData<Theme> = selectedThemeMutableLiveData

    init {
        localThemeDataSource.themeFlowable
            .mapOptional { it }
            .distinctUntilChanged()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(selectedThemeMutableLiveData::setValue)
            .disposeOnClear()
    }

    fun onThemeClicked(theme: Theme) {
        localThemeDataSource.saveTheme(theme)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { deviceThemeManager.changeDeviceTheme(theme) }
            .disposeOnClear()
    }
}
