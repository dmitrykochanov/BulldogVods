package com.dmko.bulldogvods.features.chat.data.local

import com.dmko.bulldogvods.features.chat.domain.entities.ChatPosition
import com.dmko.bulldogvods.features.chat.domain.entities.ChatTextSize
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface LocalChatDataSource {

    val chatPositionFlowable: Flowable<ChatPosition>

    val chatTextSizeFlowable: Flowable<ChatTextSize>

    fun saveLandscapeChatPosition(position: ChatPosition.Landscape): Completable

    fun savePortraitChatPosition(position: ChatPosition.Portrait): Completable

    fun saveLandscapeChatVisibility(isVisible: Boolean): Completable

    fun savePortraitChatVisibility(isVisible: Boolean): Completable

    fun saveChatTextSize(size: ChatTextSize): Completable
}
