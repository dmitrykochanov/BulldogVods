package com.dmko.bulldogvods.app

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.findNavController
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionChapterChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionChatPositionChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionSearchVods
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionThemeChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionVideoSourceChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionVod
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionVodSettings
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.navigation.LongWrapper
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommand.Back
import com.dmko.bulldogvods.app.navigation.NavigationCommand.ChapterChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.ChatPositionChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.SearchVods
import com.dmko.bulldogvods.app.navigation.NavigationCommand.ThemeChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.VideoSourceChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.Vod
import com.dmko.bulldogvods.app.navigation.NavigationCommand.VodSettings
import com.dmko.bulldogvods.app.navigation.NavigationCommandSource
import com.zhuinden.liveevent.observe
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject lateinit var navigationCommandSource: NavigationCommandSource

    private var isFullscreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationCommandSource.source.observe(this, ::handleNavigationCommand)
        isFullscreen = savedInstanceState?.getBoolean(ARG_IS_FULLSCREEN) ?: false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ARG_IS_FULLSCREEN, isFullscreen)
    }

    private fun handleNavigationCommand(command: NavigationCommand) {
        Timber.i("Handling navigation command $command")
        val navDirections = when (command) {
            is Vod -> actionVod(command.vodId, command.startOffset?.inWholeMilliseconds?.let(::LongWrapper))
            is SearchVods -> actionSearchVods()
            is ChapterChooser -> actionChapterChooser(command.vodId)
            is VodSettings -> actionVodSettings(command.vodId, command.selectedVideoSourceUrl)
            is VideoSourceChooser -> actionVideoSourceChooser(command.vodId, command.selectedVideoSourceUrl)
            is ChatPositionChooser -> actionChatPositionChooser()
            is ThemeChooser -> actionThemeChooser()
            is Back -> {
                findNavController(R.id.fragmentNavHost).navigateUp()
                return
            }
        }
        findNavController(R.id.fragmentNavHost).navigate(navDirections)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (isFullscreen) {
                enterFullscreen()
            } else {
                exitFullscreen()
            }
        }
    }

    fun enterFullscreen() {
        Timber.i("Entering fullscreen")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        isFullscreen = true
    }

    fun exitFullscreen() {
        Timber.i("Exiting fullscreen")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        isFullscreen = false
    }

    private companion object {

        private const val ARG_IS_FULLSCREEN = "is_fullscreen"
    }
}
