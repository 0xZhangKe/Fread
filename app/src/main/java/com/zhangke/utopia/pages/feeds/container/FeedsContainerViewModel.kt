package com.zhangke.utopia.pages.feeds.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.db.FeedsRepo
import com.zhangke.utopia.pages.feeds.adapter.FeedsPageUiStateAdapter
import com.zhangke.utopia.status.status.FetchStatusByUrisUseCase
import com.zhangke.utopia.status.status.GetStatusFeedsByUrisUseCase
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
            val feeds = feedsRepo.queryAll().map {
                feedsPageUiStateAdapter.adapt(it)
            }
            _uiState.update {
                it.copy(
                    pageUiStateList = LoadableState.success(feeds)
                )
            }
        }
    }

    fun onPageChanged(index: Int) {
        _uiState.update {
            it.copy(tabIndex = index)
        }
        updateIndexedPageToLoading(index)
        loadIndexPageStatus(index)
    }

    private fun loadIndexPageStatus(index: Int) {
        if (!_uiState.value.pageUiStateList.isSuccess) return
        val pageList = _uiState.value.pageUiStateList.requireSuccessData()
        if (pageList.isEmpty()) return
        val pageUiState = pageList[index]
        launchInViewModel {
            getStatusFeedsByUrisUseCase(pageUiState.sourceList, 20)
                .onSuccess { statusList ->
                    val newPageList = pageList.mapIndexed { i, feedsPageUiState ->
                        if (i == index) {
                            feedsPageUiState.copy(
                                feeds = LoadableState.success(statusList)
                            )
                        } else {
                            feedsPageUiState
                        }
                    }
                    _uiState.update { containerState ->
                        containerState.copy(
                            pageUiStateList = LoadableState.success(newPageList)
                        )
                    }
                }
                .onFailure {
                    updateIndexedPageToFailed(index, it)
                }
        }
    }

    private fun updateIndexedPageToLoading(index: Int) {
        if (!_uiState.value.pageUiStateList.isSuccess) return
        val pageList = _uiState.value.pageUiStateList.requireSuccessData()
        if (pageList.isEmpty()) return
        val newPageList = pageList.mapIndexed { i, feedsPageUiState ->
            if (i == index) {
                feedsPageUiState.copy(
                    feeds = LoadableState.loading()
                )
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

    private fun updateIndexedPageToFailed(index: Int, exception: Throwable) {
        if (!_uiState.value.pageUiStateList.isSuccess) return
        val pageList = _uiState.value.pageUiStateList.requireSuccessData()
        if (pageList.isEmpty()) return
        val newPageList = pageList.mapIndexed { i, feedsPageUiState ->
            if (i == index) {
                feedsPageUiState.copy(
                    feeds = LoadableState.failed(exception)
                )
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