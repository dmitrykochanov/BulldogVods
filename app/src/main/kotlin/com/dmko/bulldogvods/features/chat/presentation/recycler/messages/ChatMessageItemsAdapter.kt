package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.app.common.imageloader.ImageLoader
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemsAdapter(
    private val imageLoader: ImageLoader
) : ListAdapter<ChatMessageItem, ChatMessageItemViewHolder>(ChatMessageItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatMessageBinding.inflate(inflater, parent, false)
        return ChatMessageItemViewHolder(binding, imageLoader)
    }

    override fun onBindViewHolder(holder: ChatMessageItemViewHolder, position: Int) {
        val chatMessageItem = requireNotNull(getItem(position))
        holder.bindChatMessageItem(chatMessageItem)
    }

    override fun onViewRecycled(holder: ChatMessageItemViewHolder) {
        holder.onViewRecycled()
    }
}
