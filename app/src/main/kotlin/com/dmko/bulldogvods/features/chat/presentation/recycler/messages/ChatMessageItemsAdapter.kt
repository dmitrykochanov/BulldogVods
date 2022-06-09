package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.app.common.image.loader.DrawableMultiCallback
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemsAdapter : ListAdapter<ChatMessageItem, ChatMessageItemViewHolder>(ChatMessageItemDiffUtil()) {

    private val drawableCallbacksMap = hashMapOf<String, DrawableMultiCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatMessageBinding.inflate(inflater, parent, false)
        return ChatMessageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageItemViewHolder, position: Int) {
        val chatMessageItem = requireNotNull(getItem(position))
        holder.bindChatMessageItem(chatMessageItem)
        chatMessageItem.drawables.forEach { (url, drawable) ->
            val callback = drawableCallbacksMap.getOrPut(url) { DrawableMultiCallback(true) }
            callback.addView(holder.binding.messageTextView)
            drawable.callback = callback
            if (drawable is Animatable) {
                drawable.start()
            }
        }
    }

    override fun onViewRecycled(holder: ChatMessageItemViewHolder) {
        drawableCallbacksMap.values.forEach { callback -> callback.removeView(holder.binding.messageTextView) }
    }
}
