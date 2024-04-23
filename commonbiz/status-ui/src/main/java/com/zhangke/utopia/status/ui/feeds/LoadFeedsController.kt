package com.zhangke.utopia.status.ui.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
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
    private val interactiveHandler: InteractiveHandler,
    private val resolveRole: (BlogAuthor) -> IdentityRole,
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
            feedsRepo.refreshStatus()
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
        feedsRepo.refreshStatus()
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
            feedsRepo.refreshStatus()
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
            feedsRepo.loadMoreStatus(feeds.last().statusUiState.status.id)
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

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) =
        coroutineScope.launch {
            val role = resolveRole(status.intrinsicBlog.author)
            interactiveHandler.onStatusInteractive(role, status, uiInteraction).handleResult()
        }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            val role = resolveRole(blogAuthor)
            interactiveHandler.onUserInfoClick(role, blogAuthor).handleResult()
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        coroutineScope.launch {
            val role = resolveRole(status.intrinsicBlog.author)
            interactiveHandler.onVoted(role, status, options).handleResult()
        }
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        this.handle(
            messageFlow = _errorMessageFlow,
            openScreenFlow = _openScreenFlow,
            uiStatusUpdater = { newUiState ->
                feedsRepo.updateLocalStatus(newUiState.status)
                _uiState.update { currentUiState ->
                    val newFeeds = currentUiState.feeds.map {
                        if (it.statusUiState.status.id == newUiState.status.id) {
                            it.copy(statusUiState = newUiState)
                        } else {
                            it
                        }
                    }
                    currentUiState.copy(feeds = newFeeds)
                }
            }
        )
    }

    private fun List<CommonStatusUiState>.applyRefreshResult(
        refreshResult: RefreshResult,
    ): List<CommonStatusUiState> {
        val deletedIdsSet = refreshResult.deletedStatus
            .map { it.id }
            .toSet()
        val finalList = this.filter {
            !deletedIdsSet.contains(it.statusUiState.status.id)
        }.toMutableList()
        val items = refreshResult.newStatus.map { statusItem ->
            CommonStatusUiState(
                statusUiState = buildStatusUiState(statusItem),
                role = resolveRole(statusItem.intrinsicBlog.author),
            )
        }
        finalList.addAllIgnoreDuplicate(items)
        return finalList.sortedByDescending { it.statusUiState.status.datetime }
    }

    private fun MutableList<CommonStatusUiState>.addAllIgnoreDuplicate(
        newItems: List<CommonStatusUiState>,
    ) {
        newItems.forEach { this.addIfNotExist(it) }
    }

    private fun MutableList<CommonStatusUiState>.addIfNotExist(newItemUiState: CommonStatusUiState) {
        if (this.container { it.statusUiState.status.id == newItemUiState.statusUiState.status.id }) return
        this += newItemUiState
    }

    private fun transformCommonUiState(status: Status): CommonStatusUiState {
        val role = resolveRole(status.intrinsicBlog.author)
        return CommonStatusUiState(
            statusUiState = buildStatusUiState(status),
            role = role,
        )
    }
}
