package com.dmko.bulldogvods.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.dmko.bulldogvods.AppNavGraphDirections
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.navigation.NavigationEvent
import com.dmko.bulldogvods.app.navigation.NavigationEventSource
import com.zhuinden.liveevent.observe
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    @Inject lateinit var navigationEventSource: NavigationEventSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationEventSource.source.observe(this, ::handleNavigationEvent)
    }

    private fun handleNavigationEvent(event: NavigationEvent) {
        Timber.i("Handling navigation event $event")
        val navDirections = when (event) {
            is NavigationEvent.VodPlayback -> AppNavGraphDirections.actionVodPlayback(event.vodId)
        }
        findNavController(R.id.fragmentNavHost).navigate(navDirections)
    }
}
