package com.dmko.bulldogvods.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionChapterChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionSearchVods
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionThemeChooser
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionVodPlayback
import com.dmko.bulldogvods.AppNavGraphDirections.Companion.actionVodPlaybackSettings
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommand.Back
import com.dmko.bulldogvods.app.navigation.NavigationCommand.ChapterChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.SearchVods
import com.dmko.bulldogvods.app.navigation.NavigationCommand.ThemeChooser
import com.dmko.bulldogvods.app.navigation.NavigationCommand.VodPlayback
import com.dmko.bulldogvods.app.navigation.NavigationCommand.VodPlaybackSettings
import com.dmko.bulldogvods.app.navigation.NavigationCommandSource
import com.zhuinden.liveevent.observe
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject lateinit var navigationCommandSource: NavigationCommandSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationCommandSource.source.observe(this, ::handleNavigationCommand)
    }

    private fun handleNavigationCommand(command: NavigationCommand) {
        Timber.i("Handling navigation command $command")
        val navDirections = when (command) {
            is VodPlayback -> actionVodPlayback(command.vodId, command.startOffset.inWholeMilliseconds)
            is SearchVods -> actionSearchVods()
            is ChapterChooser -> actionChapterChooser(command.vodId)
            is VodPlaybackSettings -> actionVodPlaybackSettings(command.vodId, command.selectedVideoSourceUrl)
            is ThemeChooser -> actionThemeChooser()
            is Back -> {
                findNavController(R.id.fragmentNavHost).navigateUp()
                return
            }
        }
        findNavController(R.id.fragmentNavHost).navigate(navDirections)
    }
}
