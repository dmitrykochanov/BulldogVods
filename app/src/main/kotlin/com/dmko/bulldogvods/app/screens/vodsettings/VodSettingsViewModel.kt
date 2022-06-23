package com.dmko.bulldogvods.app.screens.vodsettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.app.navigation.NavigationCommand
import com.dmko.bulldogvods.app.navigation.NavigationCommandDispatcher
import com.dmko.bulldogvods.features.chat.data.local.LocalChatDataSource
import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import com.dmko.bulldogvods.features.chat.presentation.mapping.ChatPositionToStringMapper
import com.dmko.bulldogvods.features.chat.presentation.mapping.ChatTextSIzeToStringMapper
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import com.dmko.bulldogvods.features.vods.domain.entities.Vod
import com.dmko.bulldogvods.features.vods.presentation.mapping.VideoSourceToVideoSourceNameMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VodSettingsViewModel @Inject constructor(
    private val videoSourceToVideoSourceNameMapper: VideoSourceToVideoSourceNameMapper,
    private val chatTextSIzeToStringMapper: ChatTextSIzeToStringMapper,
    private val navigationCommandDispatcher: NavigationCommandDispatcher,
    chatPositionToStringMapper: ChatPositionToStringMapper,
    schedulers: Schedulers,
    networkVodsDataSource: NetworkVodsDataSource,
    localChatDataSource: LocalChatDataSource,
    savedStateHandle: SavedStateHandle,
) : RxViewModel() {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))
    private val selectedVideoSourceUrl = requireNotNull(savedStateHandle.get<String>(ARG_SELECTED_VIDEO_SOURCE_URL))

    private val selectedVideoSourceNameMutableLiveData = MutableLiveData<String>()
    val selectedVideoSourceNameLiveData: LiveData<String> = selectedVideoSourceNameMutableLiveData

    private val landscapeChatPositionMutableLiveData = MutableLiveData<Int>()
    val landscapeChatPositionLiveData: LiveData<Int> = landscapeChatPositionMutableLiveData

    private val portraitChatPositionMutableLiveData = MutableLiveData<Int>()
    val portraitChatPositionLiveData: LiveData<Int> = portraitChatPositionMutableLiveData

    private val selectedChatTextSizeMutableLiveData = MutableLiveData<Int>()
    val selectedChatTextSizeLiveData: LiveData<Int> = selectedChatTextSizeMutableLiveData

    init {
        networkVodsDataSource.getVod(vodId)
            .map(::getSelectedVideoSourceName)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(selectedVideoSourceNameMutableLiveData::setValue)

        val chatPositionFlowable = localChatDataSource.chatPositionFlowable
            .replay(1)
            .autoConnect()

        chatPositionFlowable.map(ChatPosition::landscapePosition)
            .map(chatPositionToStringMapper::map)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(landscapeChatPositionMutableLiveData::setValue)
            .disposeOnClear()

        chatPositionFlowable.map(ChatPosition::portraitPosition)
            .map(chatPositionToStringMapper::map)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(portraitChatPositionMutableLiveData::setValue)
            .disposeOnClear()

        localChatDataSource.chatTextSizeFlowable
            .map(chatTextSIzeToStringMapper::map)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .subscribe(selectedChatTextSizeMutableLiveData::setValue)
            .disposeOnClear()
    }

    private fun getSelectedVideoSourceName(vod: Vod): String {
        val selectedVideoSource = vod.videoSources.first { it.url == selectedVideoSourceUrl }
        return videoSourceToVideoSourceNameMapper.map(selectedVideoSource)
    }

    fun onVideoSourceClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.Back)
        navigationCommandDispatcher.dispatch(NavigationCommand.VideoSourceChooser(vodId, selectedVideoSourceUrl))
    }

    fun onChatPositionClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.Back)
        navigationCommandDispatcher.dispatch(NavigationCommand.ChatPositionChooser)
    }

    fun onChatTextSizeClicked() {
        navigationCommandDispatcher.dispatch(NavigationCommand.Back)
        navigationCommandDispatcher.dispatch(NavigationCommand.ChatTextSizeChooser)
    }

    private companion object {

        private const val ARG_VOD_ID = "vod_id"
        private const val ARG_SELECTED_VIDEO_SOURCE_URL = "selected_video_source_url"
    }
}
