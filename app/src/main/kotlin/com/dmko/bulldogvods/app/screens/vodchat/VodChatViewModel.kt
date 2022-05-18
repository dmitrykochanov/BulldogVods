package com.dmko.bulldogvods.app.screens.vodchat

import androidx.lifecycle.SavedStateHandle
import com.dmko.bulldogvods.app.common.rx.RxViewModel
import com.dmko.bulldogvods.app.common.schedulers.Schedulers
import com.dmko.bulldogvods.features.chat.data.network.datasource.NetworkChatDataSource
import com.dmko.bulldogvods.features.chat.domain.usecases.GetChatMessagesByPlaybackPositionUseCase
import com.dmko.bulldogvods.features.vods.data.network.datasource.NetworkVodsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VodChatViewModel @Inject constructor(
    getChatMessagesByPlaybackPositionUseCase: GetChatMessagesByPlaybackPositionUseCase,
    networkVodsDataSource: NetworkVodsDataSource,
    networkChatDataSource: NetworkChatDataSource,
    schedulers: Schedulers,
    savedStateHandle: SavedStateHandle
) : RxViewModel() {

    private val vodId = requireNotNull(savedStateHandle.get<String>(ARG_VOD_ID))

    companion object {

        const val ARG_VOD_ID = "vod_id"
    }
}
