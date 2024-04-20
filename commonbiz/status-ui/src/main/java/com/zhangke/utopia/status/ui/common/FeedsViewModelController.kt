package com.zhangke.utopia.status.ui.common

import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedsViewModelController(
    private val coroutineScope: CoroutineScope,
    private val loadFirstPageLocalFeeds: suspend () -> List<Status>,
    private val loadFromServerFunction: suspend () -> Result<List<Status>>,
    private val loadMoreFunction: suspend () -> Result<List<Status>>,
    private val getRoleFromStatus: (Status) -> IdentityRole,
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

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        coroutineScope.launch {
            while (true) {
                delay(StatusConfigurationDefault.config.autoFetchNewerFeedsInterval)
                autoFetchNewerFeeds()
            }
        }
    }

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
                val localStatus = loadFirstPageLocalFeeds()
                    .map(::transformCommonUiState)
                if (localStatus.isNotEmpty()) {
                    _uiState.update { state ->
                        state.copy(
                            feeds = localStatus,
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
            }
            loadFromServerFunction()
                .map { it.map(::transformCommonUiState) }
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
                            feeds = it,
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
        }
    }

    private suspend fun autoFetchNewerFeeds() {
        loadFromServerFunction()
            .map { it.map(::transformCommonUiState) }
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
            val role = statusProvider.statusSourceResolver
                .resolveRoleByUri(statusItem.intrinsicBlog.author.uri)
            CommonStatusUiState(role, buildStatusUiState(statusItem))
        }
        finalList.addAllIgnoreDuplicate(items)
        return finalList.sortedByDescending { it.status.status.datetime }
    }

    private fun MutableList<CommonStatusUiState>.addAllIgnoreDuplicate(
        newItems: List<CommonStatusUiState>,
    ) {
        newItems.forEach { this.addIfNotExist(it) }
    }

    private fun MutableList<CommonStatusUiState>.addIfNotExist(newItemUiState: CommonStatusUiState) {
        if (this.container { it.status.status.id == newItemUiState.status.status.id }) return
        this += newItemUiState
    }

    private fun transformCommonUiState(status: Status): CommonStatusUiState {
        val role = getRoleFromStatus(status)
        return CommonStatusUiState(
            status = buildStatusUiState(status),
            role = role,
        )
    }
}

data class CommonFeedsUiState(
    val feeds: List<CommonStatusUiState>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
)

data class CommonStatusUiState(
    val status: StatusUiState,
    val role: IdentityRole,
)
