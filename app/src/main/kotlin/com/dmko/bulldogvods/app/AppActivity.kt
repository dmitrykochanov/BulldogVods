package com.dmko.bulldogvods.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.dmko.bulldogvods.AppNavGraphDirections
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.navigation.NavigationCommand
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
            is NavigationCommand.VodPlayback -> AppNavGraphDirections.actionVodPlayback(
                vodId = command.vodId,
                startOffsetMillis = command.startOffset.inWholeMilliseconds
            )
            is NavigationCommand.SearchVods -> AppNavGraphDirections.actionSearchVods()
            is NavigationCommand.ChapterChooser -> AppNavGraphDirections.actionChapterChooser(command.vodId)
            is NavigationCommand.VodPlaybackSettings -> AppNavGraphDirections.actionVodPlaybackSettings(
                vodId = command.vodId,
                selectedVideoSourceUrl = command.selectedVideoSourceUrl
            )
            is NavigationCommand.Back -> {
                findNavController(R.id.fragmentNavHost).navigateUp()
                return
            }
        }
        findNavController(R.id.fragmentNavHost).navigate(navDirections)
    }
}
