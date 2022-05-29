package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.recyclerview.widget.DiffUtil
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemDiffUtil : DiffUtil.ItemCallback<ChatMessageItem>() {

    override fun areItemsTheSame(oldItem: ChatMessageItem, newItem: ChatMessageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageItem, newItem: ChatMessageItem): Boolean {
        return oldItem == newItem
    }
}
