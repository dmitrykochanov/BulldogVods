package com.dmko.bulldogvods.features.chat.presentation.recycler.messages

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import com.dmko.bulldogvods.app.common.image.loader.DrawableMultiCallback
import com.dmko.bulldogvods.databinding.ListItemChatMessageBinding
import com.dmko.bulldogvods.features.chat.presentation.entities.ChatMessageItem

class ChatMessageItemsAdapter : ListAdapter<ChatMessageItem, ChatMessageItemViewHolder>(ChatMessageItemDiffUtil()) {

    private val drawableCallbacksMap = hashMapOf<Drawable, DrawableMultiCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemChatMessageBinding.inflate(inflater, parent, false)
        return ChatMessageItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatMessageItemViewHolder, position: Int) {
        val chatMessageItem = requireNotNull(getItem(position))
        holder.bindChatMessageItem(chatMessageItem)
        holder.binding.messageTextView.startAnimations(chatMessageItem.animatedDrawables)
    }

    override fun onViewRecycled(holder: ChatMessageItemViewHolder) {
        holder.binding.messageTextView.stopAnimations()
    }

    private fun TextView.startAnimations(animatedDrawables: List<Drawable>) {
        animatedDrawables.forEach { drawable ->
            val callback = drawableCallbacksMap.getOrPut(drawable) { DrawableMultiCallback(true) }
            callback.addView(this)
            drawable.callback = callback
            if (drawable is Animatable && !drawable.isRunning) {
                drawable.start()
            }
        }
    }

    private fun TextView.stopAnimations() {
        drawableCallbacksMap.forEach { (drawable, callback) ->
            callback.removeView(this)
            if (callback.viewCount == 0) {
                (drawable as? Animatable)?.stop()
            }
        }
        drawableCallbacksMap.entries.removeAll { it.value.viewCount == 0 }
    }
}
