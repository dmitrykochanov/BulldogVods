package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemViewHolder(
    val binding: ListItemChatMessageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindChatMessageItem(chatMessageItem: ChatMessageItem) {
        binding.messageTextView.text = chatMessageItem.text
    }
}
