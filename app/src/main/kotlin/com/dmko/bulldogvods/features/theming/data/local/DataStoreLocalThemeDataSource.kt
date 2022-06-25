package com.dmko.bulldogvods.features.theming.data.local

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import com.dmko.bulldogvods.app.common.extensions.updateData
import com.dmko.bulldogvods.features.theming.domain.entities.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Optional
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DataStoreLocalThemeDataSource @Inject constructor(
    @ApplicationContext context: Context
) : LocalThemeDataSource {

    private val dataStore = RxPreferenceDataStoreBuilder(context.applicationContext, THEME_PREFERENCES_NAME).build()

    override val themeFlowable: Flowable<Optional<Theme>>
        get() {
            return dataStore.data()
                .map { prefs ->
                    val themeValue = prefs[KEY_THEME]
                    when (themeValue) {
                        THEME_VALUE_DARK -> Optional.of(Theme.DARK)
                        THEME_VALUE_LIGHT -> Optional.of(Theme.LIGHT)
                        THEME_VALUE_SYSTEM_DEFAULT -> Optional.of(Theme.SYSTEM_DEFAULT)
                        null -> Optional.empty()
                        else -> throw IllegalStateException("Unknown theme value $themeValue")
                    }
                }
        }

    override fun saveTheme(theme: Theme): Completable {
        return dataStore.updateData { prefs ->
            prefs[KEY_THEME] = when (theme) {
                Theme.DARK -> THEME_VALUE_DARK
                Theme.LIGHT -> THEME_VALUE_LIGHT
                Theme.SYSTEM_DEFAULT -> THEME_VALUE_SYSTEM_DEFAULT
            }
        }
    }

    private companion object {

        private const val THEME_PREFERENCES_NAME = "theme"

        private const val THEME_VALUE_DARK = "dark"
        private const val THEME_VALUE_LIGHT = "light"
        private const val THEME_VALUE_SYSTEM_DEFAULT = "system_default"

        private val KEY_THEME = stringPreferencesKey("theme")
    }
}
