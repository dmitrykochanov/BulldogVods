package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage

class ChatMessagesAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessageDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatMessageBinding.inflate(inflater, parent, false)
        return ChatMessageViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = requireNotNull(getItem(position))
        holder.bindChatMessage(chatMessage)
    }
}
