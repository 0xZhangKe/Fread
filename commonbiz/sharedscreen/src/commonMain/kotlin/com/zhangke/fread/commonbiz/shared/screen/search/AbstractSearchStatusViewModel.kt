package com.zhangke.fread.commonbiz.shared.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.handle
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AbstractSearchStatusViewModel(
    private val statusProvider: StatusProvider,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    statusUpdater: StatusUpdater,
    refactorToNewStatus: RefactorToNewStatusUseCase,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    private val _uiState = MutableStateFlow(SearchStatusUiState.default())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { it.handleResult() },
        )
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        if (searchJob?.isActive == true) searchJob?.cancel()
        doSearch()
    }

    fun onSearchClick() {
        doSearch()
    }

    fun onLoadMore() {
        if (_uiState.value.searching) return
        if (_uiState.value.result.isEmpty()) return
        if (searchJob?.isActive == true) return
        if (loadMoreJob?.isActive == true) return
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            performSearch(
                query = _uiState.value.query,
                loadMore = true,
            ).onSuccess { statuses ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Idle,
                        result = it.result + statuses,
                    )
                }
            }.onFailure { t ->
                _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
            }
        }
    }

    private fun doSearch() {
        val query = _uiState.value.query
        if (query.isEmpty() || query.isBlank()) {
            _uiState.update { it.copy(result = emptyList()) }
            return
        }
        searchJob = launchInViewModel {
            _uiState.update { it.copy(searching = true) }
            performSearch(query = query, loadMore = false)
                .onSuccess { statuses ->
                    _uiState.update {
                        it.copy(searching = false, result = statuses)
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(searching = false) }
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    abstract suspend fun performSearch(
        query: String,
        loadMore: Boolean,
    ): Result<List<StatusUiState>>

    private suspend fun InteractiveHandleResult.handleResult() {
        handle(
            uiStatusUpdater = { newUiState ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        result = currentUiState.result.map {
                            if (it.status.intrinsicBlog.id == newUiState.status.intrinsicBlog.id) {
                                newUiState
                            } else {
                                it
                            }
                        }
                    )
                }
            },
            deleteStatus = { statusId ->
                _uiState.update { state ->
                    state.copy(
                        result = state.result.filter {
                            it.status.id != statusId
                        }
                    )
                }
            },
            followStateUpdater = { _, _ -> },
        )
    }
}