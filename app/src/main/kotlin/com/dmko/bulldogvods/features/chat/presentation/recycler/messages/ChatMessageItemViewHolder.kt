package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemViewHolder(
    private val binding: ListItemChatMessageBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bindChatMessageItem(chatMessageItem: ChatMessageItem) {
        binding.messageTextView.text = chatMessageItem.text
        chatMessageItem.spanImages.forEach { spanImage ->
            imageLoader.loadIntoSpans(
                url = spanImage.url,
                spanPositions = spanImage.spanPositions,
                target = binding.messageTextView
            )
        }
    }

    fun onViewRecycled() {
        imageLoader.dispose(binding.messageTextView)
    }
}
