package com.dmko.bulldogvods.app.screens.chattextsizechooser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.features.chat.data.local.LocalChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatTextSizeChooserViewModel @Inject constructor(
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    private val localChatDataSource: LocalChatDataSource,
    private val schedulers: Schedulers
) : RxViewModel() {

    private val chatTextSizeMutableLiveData = MutableLiveData<ChatTextSize>()
    val chatTextSizeLiveData: LiveData<ChatTextSize> = chatTextSizeMutableLiveData

    init {
        localChatDataSource.chatTextSizeFlowable
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(chatTextSizeMutableLiveData::setValue)
            .disposeOnClear()
    }

    fun onTextSizeSelected(size: ChatTextSize) {
        localChatDataSource.saveChatTextSize(size)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe { navigationCommandDispatcher.dispatch(NavigationCommand.Back) }
            .disposeOnClear()
    }
}
