package com.dmko.bulldogvods.features.chat.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DataStoreLocalChatDataSource @Inject constructor(
    @ApplicationContext context: Context
) : LocalChatDataSource {

    private val dataStore = RxPreferenceDataStoreBuilder(context.applicationContext, CHAT_PREFERENCES_NAME).build()

    override val chatPositionFlowable: Flowable<ChatPosition>
        get() {
            return dataStore.data().map { prefs ->
                val landscapePositionValue = prefs[KEY_LANDSCAPE_POSITION]
                val portraitPositionValue = prefs[KEY_PORTRAIT_POSITION]
                ChatPosition(
                    landscapePosition = when (landscapePositionValue) {
                        VALUE_LANDSCAPE_POSITION_LEFT -> ChatPosition.Landscape.LEFT
                        VALUE_LANDSCAPE_POSITION_RIGHT -> ChatPosition.Landscape.RIGHT
                        VALUE_LANDSCAPE_POSITION_LEFT_OVERLAY -> ChatPosition.Landscape.LEFT_OVERLAY
                        VALUE_LANDSCAPE_POSITION_RIGHT_OVERLAY -> ChatPosition.Landscape.RIGHT_OVERLAY
                        VALUE_LANDSCAPE_POSITION_TOP_LEFT_OVERLAY -> ChatPosition.Landscape.TOP_LEFT_OVERLAY
                        VALUE_LANDSCAPE_POSITION_TOP_RIGHT_OVERLAY -> ChatPosition.Landscape.TOP_RIGHT_OVERLAY
                        VALUE_LANDSCAPE_POSITION_BOTTOM_LEFT_OVERLAY -> ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY
                        VALUE_LANDSCAPE_POSITION_BOTTOM_RIGHT_OVERLAY -> ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY
                        null -> ChatPosition.Landscape.RIGHT
                        else -> throw IllegalStateException("Unknown landscape position value $landscapePositionValue")
                    },
                    portraitPosition = when (portraitPositionValue) {
                        VALUE_PORTRAIT_POSITION_TOP -> ChatPosition.Portrait.TOP
                        VALUE_PORTRAIT_POSITION_BOTTOM -> ChatPosition.Portrait.BOTTOM
                        null -> ChatPosition.Portrait.BOTTOM
                        else -> throw IllegalStateException("Unknown portrait position value $portraitPositionValue")
                    },
                    isVisibleInLandscape = prefs[KEY_LANDSCAPE_VISIBILITY] ?: true,
                    isVisibleInPortrait = prefs[KEY_PORTRAIT_VISIBILITY] ?: true
                )
            }
        }

    override val chatTextSizeFlowable: Flowable<ChatTextSize>
        get() {
            return dataStore.data().map { prefs ->
                val textSizeValue = prefs[KEY_TEXT_SIZE]
                when (textSizeValue) {
                    VALUE_TEXT_SIZE_SMALL -> ChatTextSize.SMALL
                    VALUE_TEXT_SIZE_NORMAL -> ChatTextSize.NORMAL
                    VALUE_TEXT_SIZE_LARGE -> ChatTextSize.LARGE
                    VALUE_TEXT_SIZE_HUGE -> ChatTextSize.HUGE
                    null -> ChatTextSize.NORMAL
                    else -> throw IllegalStateException("Unknown text size value $textSizeValue")
                }
            }
        }

    override val chatWidthPercentageFlowable: Flowable<Float>
        get() {
            return dataStore.data().map { prefs ->
                prefs[KEY_WIDTH] ?: DEFAULT_WIDTH_PERCENTAGE
            }
        }

    override fun saveLandscapeChatPosition(position: ChatPosition.Landscape): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_LANDSCAPE_POSITION] = when (position) {
                ChatPosition.Landscape.LEFT -> VALUE_LANDSCAPE_POSITION_LEFT
                ChatPosition.Landscape.RIGHT -> VALUE_LANDSCAPE_POSITION_RIGHT
                ChatPosition.Landscape.LEFT_OVERLAY -> VALUE_LANDSCAPE_POSITION_LEFT_OVERLAY
                ChatPosition.Landscape.RIGHT_OVERLAY -> VALUE_LANDSCAPE_POSITION_RIGHT_OVERLAY
                ChatPosition.Landscape.TOP_LEFT_OVERLAY -> VALUE_LANDSCAPE_POSITION_TOP_LEFT_OVERLAY
                ChatPosition.Landscape.TOP_RIGHT_OVERLAY -> VALUE_LANDSCAPE_POSITION_TOP_RIGHT_OVERLAY
                ChatPosition.Landscape.BOTTOM_LEFT_OVERLAY -> VALUE_LANDSCAPE_POSITION_BOTTOM_LEFT_OVERLAY
                ChatPosition.Landscape.BOTTOM_RIGHT_OVERLAY -> VALUE_LANDSCAPE_POSITION_BOTTOM_RIGHT_OVERLAY
            }
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    override fun savePortraitChatPosition(position: ChatPosition.Portrait): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_PORTRAIT_POSITION] = when (position) {
                ChatPosition.Portrait.TOP -> VALUE_PORTRAIT_POSITION_TOP
                ChatPosition.Portrait.BOTTOM -> VALUE_PORTRAIT_POSITION_BOTTOM
            }
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    override fun saveLandscapeChatVisibility(isVisible: Boolean): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_LANDSCAPE_VISIBILITY] = isVisible
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    override fun savePortraitChatVisibility(isVisible: Boolean): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_PORTRAIT_VISIBILITY] = isVisible
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    override fun saveChatTextSize(size: ChatTextSize): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_TEXT_SIZE] = when (size) {
                ChatTextSize.SMALL -> VALUE_TEXT_SIZE_SMALL
                ChatTextSize.NORMAL -> VALUE_TEXT_SIZE_NORMAL
                ChatTextSize.LARGE -> VALUE_TEXT_SIZE_LARGE
                ChatTextSize.HUGE -> VALUE_TEXT_SIZE_HUGE
            }
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    override fun saveChatWidthPercentage(widthPercentage: Float): Completable {
        return dataStore.updateDataAsync { prefs ->
            val mutablePrefs = prefs.toMutablePreferences()
            mutablePrefs[KEY_WIDTH] = widthPercentage
            Single.just(mutablePrefs)
        }
            .ignoreElement()
    }

    private companion object {

        private const val CHAT_PREFERENCES_NAME = "chat"

        private const val VALUE_LANDSCAPE_POSITION_LEFT = "left"
        private const val VALUE_LANDSCAPE_POSITION_RIGHT = "right"
        private const val VALUE_LANDSCAPE_POSITION_LEFT_OVERLAY = "left_overlay"
        private const val VALUE_LANDSCAPE_POSITION_RIGHT_OVERLAY = "right_overlay"
        private const val VALUE_LANDSCAPE_POSITION_TOP_LEFT_OVERLAY = "top_left_overlay"
        private const val VALUE_LANDSCAPE_POSITION_TOP_RIGHT_OVERLAY = "top_right_overlay"
        private const val VALUE_LANDSCAPE_POSITION_BOTTOM_LEFT_OVERLAY = "bottom_left_overlay"
        private const val VALUE_LANDSCAPE_POSITION_BOTTOM_RIGHT_OVERLAY = "bottom_right_overlay"

        private const val VALUE_PORTRAIT_POSITION_TOP = "top"
        private const val VALUE_PORTRAIT_POSITION_BOTTOM = "bottom"

        private const val VALUE_TEXT_SIZE_SMALL = "small"
        private const val VALUE_TEXT_SIZE_NORMAL = "normal"
        private const val VALUE_TEXT_SIZE_LARGE = "large"
        private const val VALUE_TEXT_SIZE_HUGE = "huge"

        private const val DEFAULT_WIDTH_PERCENTAGE = 25f

        private val KEY_LANDSCAPE_POSITION = stringPreferencesKey("landscape_position")
        private val KEY_PORTRAIT_POSITION = stringPreferencesKey("portrait_position")
        private val KEY_LANDSCAPE_VISIBILITY = booleanPreferencesKey("landscape_visibility")
        private val KEY_PORTRAIT_VISIBILITY = booleanPreferencesKey("portrait_visibility")
        private val KEY_TEXT_SIZE = stringPreferencesKey("text_size")
        private val KEY_WIDTH = floatPreferencesKey("width")
    }
}
