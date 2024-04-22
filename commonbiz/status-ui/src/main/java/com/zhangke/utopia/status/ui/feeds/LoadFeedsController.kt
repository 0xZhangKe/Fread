package com.zhangke.utopia.status.ui.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoadFeedsController(
    private val coroutineScope: CoroutineScope,
    private val feedsRepo: AbstractFeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private val _uiState = MutableStateFlow(
        CommonFeedsUiState(
            feeds = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _newStatusNotifyFlow = MutableSharedFlow<Unit>()
    val newStatusNotifyFlow = _newStatusNotifyFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    private var autoFetchNewerFeedsJob: Job? = null

    fun initFeeds(needLocalData: Boolean) {
        initFeedsJob?.cancel()
        initFeedsJob = coroutineScope.launch {
            _uiState.update {
                it.copy(
                    showPagingLoadingPlaceholder = true,
                    pageErrorContent = null,
                    feeds = emptyList(),
                )
            }
            if (needLocalData) {
                feedsRepo.getLocalStatus()
                    .map { it.map(::transformCommonUiState) }
                    .onSuccess { localStatus ->
                        if (localStatus.isNotEmpty()) {
                            _uiState.update { state ->
                                state.copy(
                                    feeds = localStatus,
                                    showPagingLoadingPlaceholder = false,
                                )
                            }
                        }
                    }
            }
            loadNewFromServerFunction()
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = if (state.feeds.isEmpty()) {
                                it.toTextStringOrNull()
                            } else {
                                null
                            },
                        )
                    }
                    if (_uiState.value.feeds.isNotEmpty()) {
                        _errorMessageFlow.emitTextMessageFromThrowable(it)
                    }
                }.onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            feeds = it.newStatus.map(::transformCommonUiState),
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
        }
    }

    fun startAutoFetchNewerFeeds() {
        if (autoFetchNewerFeedsJob != null) return
        autoFetchNewerFeedsJob = coroutineScope.launch {
            while (true) {
                delay(StatusConfigurationDefault.config.autoFetchNewerFeedsInterval)
                autoFetchNewerFeeds()
            }
        }
    }

    private suspend fun autoFetchNewerFeeds() {
        loadNewFromServerFunction()
            .onSuccess {
                _uiState.update { state ->
                    state.copy(
                        feeds = state.feeds.applyRefreshResult(it),
                    )
                }
                if (it.newStatus.isNotEmpty()) {
                    _newStatusNotifyFlow.emit(Unit)
                }
            }
    }

    fun refresh() {
        val uiState = _uiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        refreshJob?.cancel()
        refreshJob = coroutineScope.launch {
            _uiState.update { it.copy(refreshing = true) }
            loadNewFromServerFunction()
                .onSuccess { refreshResult ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            feeds = it.feeds.applyRefreshResult(refreshResult),
                        )
                    }

                }.onFailure { e ->
                    _errorMessageFlow.emitTextMessageFromThrowable(e)
                    _uiState.update {
                        it.copy(refreshing = false)
                    }
                }
        }
    }

    fun loadMore() {
        val uiState = _uiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        loadMoreJob?.cancel()
        loadMoreJob = coroutineScope.launch {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            loadMoreFunction(feeds.last().statusUiState.status.id)
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            loadMoreState = LoadState.Failed(e.toTextStringOrNull()),
                        )
                    }
                }.onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            loadMoreState = LoadState.Idle,
                            feeds = it.feeds.toMutableList().apply {
                                addAllIgnoreDuplicate(list.map(::transformCommonUiState))
                            },
                        )
                    }
                }
        }
    }

}
