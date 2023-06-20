package com.zhangke.utopia.pages.feeds.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.db.FeedsRepo
import com.zhangke.utopia.pages.feeds.FeedsPageUiState
import com.zhangke.utopia.pages.feeds.adapter.FeedsPageUiStateAdapter
import com.zhangke.utopia.status.status.GetStatusFeedsByUrisUseCase
import com.zhangke.utopia.status.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsContainerViewModel @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val feedsPageUiStateAdapter: FeedsPageUiStateAdapter,
    private val getStatusFeedsByUrisUseCase: GetStatusFeedsByUrisUseCase,
) : ViewModel() {

    private val pagedFetchers = mutableMapOf<Int, FeedsFetcher<Status>>()

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<FeedsContainerUiState> = _uiState.asStateFlow()

    init {
        loadTabs()
        onPageChanged(0)
    }

    private fun loadTabs() {
        _uiState.update {
            it.copy(
                pageUiStateList = LoadableState.loading()
            )
        }
        viewModelScope.launch {
            pagedFetchers.clear()
            val pageStates = feedsRepo.queryAll().mapIndexed { index, feeds ->
                val fetcher = getStatusFeedsByUrisUseCase(feeds.sourceList, 20)
                pagedFetchers[index] = fetcher
                feedsPageUiStateAdapter.adapt(feeds, fetcher.dataFlow)
            }
            _uiState.update {
                it.copy(
                    pageUiStateList = LoadableState.success(pageStates)
                )
            }
        }
    }

    fun onPageChanged(index: Int) {
        _uiState.update {
            it.copy(tabIndex = index)
        }
        updateIndexedPageToRefreshing(index)
        loadIndexPageStatus(index)
    }

    private fun loadIndexPageStatus(index: Int) {
        if (!_uiState.value.pageUiStateList.isSuccess) return
        val pageList = _uiState.value.pageUiStateList.requireSuccessData()
        if (pageList.isEmpty()) return
        val fetcher = pagedFetchers[index]!!

        updateIndexedPageToLoading(index)
        launchInViewModel {
            fetcher.loadNextPage()
                .onSuccess {
                    updateIndexedPageToData(index)
                }.onFailure {
                    updateIndexedPageToData(index)
                }
        }
    }

    private fun updateIndexedPageToRefreshing(index: Int) {
        updateIndexedPage(index) {
            it.copy(
                refreshing = true,
                loading = false,
            )
        }
    }

    private fun updateIndexedPageToLoading(index: Int) {
        updateIndexedPage(index) {
            it.copy(
                refreshing = false,
                loading = true,
            )
        }
    }

    private fun updateIndexedPageToData(index: Int) {
        updateIndexedPage(index) {
            it.copy(
                refreshing = false,
                loading = false,
            )
        }
    }

    private fun updateIndexedPage(index: Int, block: (FeedsPageUiState) -> FeedsPageUiState) {
        if (!_uiState.value.pageUiStateList.isSuccess) return
        val pageList = _uiState.value.pageUiStateList.requireSuccessData()
        if (pageList.isEmpty()) return
        val newPageList = pageList.mapIndexed { i, feedsPageUiState ->
            if (i == index) {
                block(feedsPageUiState)
            } else {
                feedsPageUiState
            }
        }
        _uiState.update {
            it.copy(
                pageUiStateList = LoadableState.success(newPageList)
            )
        }
    }

    private fun initialState(): FeedsContainerUiState {
        return FeedsContainerUiState(
            pageUiStateList = LoadableState.loading(),
            tabIndex = 0,
        )
    }
}