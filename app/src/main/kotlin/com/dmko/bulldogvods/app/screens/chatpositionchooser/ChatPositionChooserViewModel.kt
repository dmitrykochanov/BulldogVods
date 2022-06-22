package com.dmko.bulldogvods.app.screens.chatpositionchooser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.features.chat.data.local.LocalChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatPositionChooserViewModel @Inject constructor(
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val localChatDataSource: LocalChatDataSource,
    private val schedulers: Schedulers
) : RxViewModel() {

    private val landscapePositionMutableLiveData = MutableLiveData<ChatPosition.Landscape>()
    val landscapePositionLiveData: LiveData<ChatPosition.Landscape> = landscapePositionMutableLiveData

    private val portraitPositionMutableLiveData = MutableLiveData<ChatPosition.Portrait>()
    val portraitPositionLiveData: LiveData<ChatPosition.Portrait> = portraitPositionMutableLiveData

    init {
        val chatPositionFlowable = localChatDataSource.chatPositionFlowable
            .replay(1)
            .autoConnect()

        chatPositionFlowable.map(ChatPosition::landscapePosition)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(landscapePositionMutableLiveData::setValue)
            .disposeOnClear()

        chatPositionFlowable.map(ChatPosition::portraitPosition)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(portraitPositionMutableLiveData::setValue)
            .disposeOnClear()
    }

    fun onLandscapePositionSelected(position: ChatPosition.Landscape) {
        localChatDataSource.saveLandscapeChatPosition(position)
            .andThen(localChatDataSource.saveLandscapeChatVisibility(true))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { navigationCommandDispatcher.dispatch(NavigationCommand.Back) }
            .disposeOnClear()
    }

    fun onPortraitPositionSelected(position: ChatPosition.Portrait) {
        localChatDataSource.savePortraitChatPosition(position)
            .andThen(localChatDataSource.savePortraitChatVisibility(true))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { navigationCommandDispatcher.dispatch(NavigationCommand.Back) }
            .disposeOnClear()
    }
}
