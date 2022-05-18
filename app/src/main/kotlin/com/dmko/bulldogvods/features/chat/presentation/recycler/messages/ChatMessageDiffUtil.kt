package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.recyclerview.widget.DiffUtil
import com.dmko.bulldogvods.features.chat.domain.entities.ChatMessage

class ChatMessageDiffUtil : DiffUtil.ItemCallback<ChatMessage>() {

    override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem == newItem
    }
}
