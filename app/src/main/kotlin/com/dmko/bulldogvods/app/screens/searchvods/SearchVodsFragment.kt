package com.dmko.bulldogvods.app.screens.searchvods

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.dmko.bulldogvods.R
import com.dmko.bulldogvods.app.common.binding.viewBinding
import com.dmko.bulldogvods.app.common.image.loader.ImageLoader
import com.dmko.bulldogvods.app.common.keyboard.clearFocusAndHideKeyboard
import com.dmko.bulldogvods.app.common.keyboard.focusAndShowKeyboard
import com.dmko.bulldogvods.app.common.recycler.decorations.GridSpacingItemDecoration
import com.dmko.bulldogvods.app.common.recycler.paging.GenericLoadStateAdapter
import com.dmko.bulldogvods.app.common.views.OutsideTouchHelper
import com.dmko.bulldogvods.databinding.FragmentSearchVodsBinding
import com.dmko.bulldogvods.features.vods.presentation.entities.VodItem
import com.dmko.bulldogvods.features.vods.presentation.recycler.vods.VodItemsAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchVodsFragment : Fragment(R.layout.fragment_search_vods) {

    private val viewModel: SearchVodsViewModel by viewModels()
    private val binding by viewBinding(FragmentSearchVodsBinding::bind)

    @Inject lateinit var imageLoader: ImageLoader

    private lateinit var vodItemsAdapter: VodItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        setupVodsRecycler()
        binding.swipeRefreshLayout.setOnRefreshListener { vodItemsAdapter.refresh() }
        binding.layoutError.buttonRetry.setOnClickListener { vodItemsAdapter.retry() }
        binding.imageBack.setOnClickListener { viewModel.onBackClicked() }
        binding.textInputSearch.focusAndShowKeyboard()
        binding.textInputSearch.doAfterTextChanged { editable ->
            viewModel.onSearchQueryChanged(editable?.toString().orEmpty())
        }
        binding.textInputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.onSearchClicked()
                true
            } else {
                false
            }
        }
        OutsideTouchHelper(
            views = listOf(binding.textInputLayoutSearch),
            outsideTouchAction = { binding.textInputSearch.clearFocusAndHideKeyboard() }
        ).attachToCoordinatorLayout(binding.coordinatorLayout)

        viewModel.vodsPagingDataLiveData.observe(viewLifecycleOwner, ::showVodItemsPagingData)
        vodItemsAdapter.loadStateFlow.asLiveData().observe(viewLifecycleOwner, ::showVodItemsLoadState)
    }

    private fun setupVodsRecycler() {
        vodItemsAdapter = VodItemsAdapter(
            imageLoader = imageLoader,
            onVodClickListener = viewModel::onVodClicked,
            onVodChaptersClickListener = viewModel::onVodChaptersClicked
        )
        val genericLoadStateAdapter = GenericLoadStateAdapter(
            onRetryClickListener = { vodItemsAdapter.retry() }
        )
        binding.recyclerVods.adapter = vodItemsAdapter.withLoadStateFooter(genericLoadStateAdapter)

        val vodsListSpanCount = requireContext().resources.getInteger(R.integer.vods_list_span_count)
        val vodsSpacingItemDecoration = GridSpacingItemDecoration(
            spanCount = vodsListSpanCount,
            spacing = requireContext().resources.getDimensionPixelSize(R.dimen.padding_normal),
            includeLeftEdge = vodsListSpanCount > 1,
            includeRightEdge = vodsListSpanCount > 1,
            includeTopEdge = false
        )
        binding.recyclerVods.addItemDecoration(vodsSpacingItemDecoration)
    }

    private fun showVodItemsPagingData(vodItemsPagingData: PagingData<VodItem>) {
        vodItemsAdapter.submitData(lifecycle, vodItemsPagingData)
    }

    private fun showVodItemsLoadState(loadingState: CombinedLoadStates) {
        val refreshState = loadingState.refresh
        when (refreshState) {
            is LoadState.Loading -> {
                binding.swipeRefreshLayout.isRefreshing = true
                binding.recyclerVods.isVisible = false
                binding.layoutEmptyVodsSearch.root.isVisible = false
                binding.layoutError.root.isVisible = false
            }
            is LoadState.NotLoading -> {
                binding.swipeRefreshLayout.isRefreshing = false
                if (vodItemsAdapter.itemCount == 0 && binding.textInputSearch.text?.isNotEmpty() == true) {
                    binding.recyclerVods.isVisible = false
                    binding.layoutEmptyVodsSearch.root.isVisible = true
                } else {
                    binding.recyclerVods.isVisible = true
                    binding.layoutEmptyVodsSearch.root.isVisible = false
                }
                binding.layoutError.root.isVisible = false
            }
            is LoadState.Error -> {
                binding.swipeRefreshLayout.isRefreshing = false
                binding.recyclerVods.isVisible = false
                binding.layoutEmptyVodsSearch.root.isVisible = false
                binding.layoutError.root.isVisible = true
                Timber.e(refreshState.error, "Failed to search vods")
            }
        }
    }
}
