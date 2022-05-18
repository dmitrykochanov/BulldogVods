package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage

class ChatMessageViewHolder(
    private val binding: ListItemChatMessageBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindChatMessage(chatMessage: ChatMessage) {
        binding.messageTextView.text = "${chatMessage.user.name}: ${chatMessage.text}"
    }
}
