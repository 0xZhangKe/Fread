package com.zhangke.fread.activitypub.app.internal.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.handle
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class SearchStatusViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusProvider: StatusProvider,
    private val platformRepo: ActivityPubPlatformRepo,
    loggedAccountProvider: LoggedAccountProvider,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    statusUpdater: StatusUpdater,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val userId: String,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator, userId: String): SearchStatusViewModel
    }

    private val _uiState = MutableStateFlow(SearchStatusUiState.default())
    val uiState = _uiState.asStateFlow()

    private var platform: BlogPlatform? = null
    private var loggedAccount: ActivityPubLoggedAccount? = loggedAccountProvider.getAccount(locator)
    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        launchInViewModel { platform = loadPlatform().getOrNull() }
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { it.handleResult() },
        )
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        doSearch()
    }

    fun onSearchClick() {
        doSearch()
    }

    fun onLoadMore() {
        if (_uiState.value.searching) return
        if (_uiState.value.result.isEmpty()) return
        if (loadMoreJob?.isActive == true) return
        val maxId = _uiState.value.result.last().status.id
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            loadStatus(
                query = _uiState.value.query,
                maxId = maxId,
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
            loadStatus(query = query)
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

    private suspend fun loadStatus(
        query: String,
        maxId: String? = null,
    ): Result<List<StatusUiState>> {
        val platformResult = loadPlatform()
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(locator)
            .searchRepo
            .queryStatus(
                query = query,
                accountId = userId,
                maxId = maxId,
                limit = 20,
            ).map { statuses ->
                statuses.map {
                    statusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        locator = locator,
                        loggedAccount = loggedAccount,
                    )
                }
            }
    }

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

    private suspend fun loadPlatform(): Result<BlogPlatform> {
        if (platform != null) return Result.success(platform!!)
        return platformRepo.getPlatform(locator)
            .onSuccess { platform = it }
    }
}
