package com.zhangke.utopia.feeds.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.feeds.fetcher.FeedsFetcher
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.feeds.adapter.FeedsPageUiStateAdapter
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.feeds.repo.db.FeedsRepo
import com.zhangke.utopia.status.source.ResolveStatusSourceUseCase
import com.zhangke.utopia.status.status.GetStatusFeedsByUrisUseCase
import com.zhangke.utopia.status.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FeedsHomeViewModel @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val feedsPageUiStateAdapter: FeedsPageUiStateAdapter,
    private val getStatusFeedsByUrisUseCase: GetStatusFeedsByUrisUseCase,
    private val resolveStatusSource: ResolveStatusSourceUseCase,
) : ViewModel() {

    private val pagedFetchers = mutableMapOf<Int, FeedsFetcher<Status>>()

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<FeedsHomeUiState> = _uiState.asStateFlow()

    init {
        loadTabs()
    }

    private fun loadTabs() {
        _uiState.update {
            it.copy(
                pageUiStateList = LoadableState.loading()
            )
        }
        viewModelScope.launch {
            pagedFetchers.clear()
            val feedsList = feedsRepo.queryAll()
            val pageStates = feedsList.mapIndexed { index, feeds ->
                val sourceList = feeds.sourceList
                    .mapNotNull { uri -> resolveStatusSource(uri).getOrNull() }
                val fetcher = getStatusFeedsByUrisUseCase(feeds.sourceList, 20)
                pagedFetchers[index] = fetcher
                feedsPageUiStateAdapter.adapt(
                    feeds.name,
                    sourceList,
                    fetcher.dataFlow,
                )
            }
            _uiState.update {
                it.copy(
                    pageUiStateList = LoadableState.success(pageStates)
                )
            }
            refreshPage(_uiState.value.tabIndex)
        }
    }

    fun onPageChanged(index: Int) {
        if (index == _uiState.value.tabIndex) return
        _uiState.update {
            it.copy(tabIndex = index)
        }
        refreshPage(index)
    }

    private fun refreshPage(index: Int) {
        updateIndexedPageToRefreshing(index)
        val fetcher = pagedFetchers[index]!!
        launchInViewModel(Dispatchers.IO) {
            fetcher.refresh()
                .onSuccess { updateIndexedPageToData(index) }
                .onFailure {
                    updateIndexedPageToData(index)
                    updateIndexedPageSnackMessage(index, it.message)
                }
        }
    }

    fun onRefresh() {
        val index = _uiState.value.tabIndex
        refreshPage(index)
    }

    fun onLoadMore() {
        val currentPageIndex = _uiState.value.tabIndex
        val fetcher = pagedFetchers[currentPageIndex]!!
        launchInViewModel(Dispatchers.IO) {
            updateIndexedPageToLoading(currentPageIndex)
            fetcher.loadNextPage()
                .onSuccess { updateIndexedPageToData(currentPageIndex) }
                .onFailure {
                    updateIndexedPageToLoadError(currentPageIndex)
                    updateIndexedPageSnackMessage(currentPageIndex, it.message)
                }
        }
    }

    private fun updateIndexedPageToRefreshing(index: Int) {
        updateIndexedPage(index) {
            it.copy(refreshing = true)
        }
    }

    private fun updateIndexedPageToLoading(index: Int) {
        updateIndexedPage(index) {
            it.copy(loading = true)
        }
    }

    private fun updateIndexedPageToLoadError(index: Int) {
        updateIndexedPage(index) {
            it.copy(
                loading = false,
                loadMoreError = true,
            )
        }
    }

    private fun updateIndexedPageToData(index: Int) {
        updateIndexedPage(index) {
            it.copy(
                refreshing = false,
                loading = false,
                loadMoreError = false,
            )
        }
    }

    private fun updateIndexedPageSnackMessage(index: Int, snackMessage: String?) {
        updateIndexedPage(index) {
            it.copy(snackMessage = snackMessage?.let(::textOf))
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

    private fun initialState(): FeedsHomeUiState {
        return FeedsHomeUiState(
            pageUiStateList = LoadableState.loading(),
            tabIndex = 0,
        )
    }
}
