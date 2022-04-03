package com.dmko.bulldogvods.app.common.recycler.paging

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.dmko.bulldogvods.databinding.ListItemGenericLoadStateBinding

class GenericLoadStateViewHolder(
    private val binding: ListItemGenericLoadStateBinding
) : RecyclerView.ViewHolder(binding.root) {


    fun bindLoadState(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.layoutGenericError.isVisible = loadState is LoadState.Error
    }

    fun setOnRetryClickListener(onRetryClickListener: () -> Unit) {
        binding.buttonRetry.setOnClickListener { onRetryClickListener.invoke() }
    }
}
