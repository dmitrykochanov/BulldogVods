package com.dmko.bulldogvods.app.common.recycler.paging

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.dmko.bulldogvods.databinding.ListItemGenericLoadStateBinding

class GenericLoadStateAdapter(
    private val onRetryClickListener: () -> Unit
) : LoadStateAdapter<GenericLoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): GenericLoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemGenericLoadStateBinding.inflate(inflater, parent, false)
        return GenericLoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericLoadStateViewHolder, loadState: LoadState) {
        holder.bindLoadState(loadState)
        holder.setOnRetryClickListener(onRetryClickListener)
    }
}
