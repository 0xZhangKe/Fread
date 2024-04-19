package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.richtext.preParseRichText
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update

class MixedContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val configId: Long,
    private val interactiveHandler: InteractiveHandler,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(MixedContentUiState.initialUiState)
    val uiState: StateFlow<MixedContentUiState> get() = _uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    private val _newStatusNotifyFlow = MutableSharedFlow<Unit>()
    val newStatusNotifyFlow = _newStatusNotifyFlow.asSharedFlow()

    private var mixedContent: ContentConfig.MixedContent? = null

    private val config = StatusConfigurationDefault.config

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect {
                    delay(200)
                    initFeeds(false)
                }
        }
        launchInViewModel {
            feedsRepo.feedsInfoChangedFlow
                .collect {
                    delay(50)
                    initFeeds(false)
                }
        }
        launchInViewModel {
            contentConfigRepo.getConfigFlow(configId)
                .drop(1)
                .collect {
                    delay(50)
                    initFeeds(false)
                }
        }
        launchInViewModel {
            while (true) {
                delay(StatusConfigurationDefault.config.autoFetchNewerFeedsInterval)
                autoFetchNewerFeeds()
            }
        }
        initFeeds(true)
    }

    private fun initFeeds(needLocalData: Boolean) {
        initFeedsJob?.cancel()
        initFeedsJob = launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            if (mixedContent == null) {
                _uiState.update { it.copy(pageErrorContent = textOf(R.string.feeds_mixed_config_not_found)) }
                return@launchInViewModel
            }
            _uiState.update {
                it.copy(
                    showPagingLoadingPlaceholder = true,
                    pageErrorContent = null,
                    feeds = emptyList(),
                )
            }
            val sourceList = mixedContent!!.sourceUriList
            if (needLocalData) {
                val localStatus = feedsRepo.getLocalFirstPageStatus(
                    sourceUriList = sourceList,
                    limit = config.loadFromLocalLimit,
                )
                val newFeeds = localStatus.map {
                    val statusUiState = buildStatusUiState(it)
                    val role = statusProvider.statusSourceResolver
                        .resolveRoleByUri(it.intrinsicBlog.author.uri)
                    MixedContentItemUiState(role, statusUiState)
                }
                _uiState.update { state ->
                    state.copy(
                        feeds = newFeeds,
                        showPagingLoadingPlaceholder = false,
                        pageErrorContent = null,
                    )
                }
            }
            feedsRepo.refresh(
                sourceUriList = sourceList,
                limit = config.loadFromServerLimit,
            ).onFailure {
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
                        feeds = state.feeds.applyRefreshResult(it),
                        pageErrorContent = null,
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
        }
    }

    private suspend fun autoFetchNewerFeeds() {
        val sourceList = mixedContent?.sourceUriList ?: return
        feedsRepo.refresh(
            sourceUriList = sourceList,
            limit = config.loadFromServerLimit,
        ).onSuccess {
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

    fun onRefresh() {
        val uiState = _uiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            feedsRepo.refresh(
                sourceUriList = sourceList,
                limit = config.loadFromServerLimit,
            ).map {
                it.newStatus.preParseRichText()
                it
            }.onSuccess { refreshResult ->
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

    private fun List<MixedContentItemUiState>.applyRefreshResult(
        refreshResult: RefreshResult,
    ): List<MixedContentItemUiState> {
        val deletedIdsSet = refreshResult.deletedStatus
            .map { it.id }
            .toSet()
        val finalList = this.filter {
            !deletedIdsSet.contains(it.statusUiState.status.id)
        }.toMutableList()
        val items = refreshResult.newStatus.map { statusItem ->
            val role = statusProvider.statusSourceResolver
                .resolveRoleByUri(statusItem.intrinsicBlog.author.uri)
            MixedContentItemUiState(role, buildStatusUiState(statusItem))
        }
        finalList.addAllIgnoreDuplicate(items)
        return finalList.sortedByDescending { it.statusUiState.status.datetime }
    }

    fun onLoadMore() {
        val uiState = _uiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            feedsRepo.getStatus(
                sourceUriList = sourceList,
                limit = config.loadFromServerLimit,
                maxId = feeds.last().statusUiState.status.id,
            ).map { statusList ->
                statusList.preParseRichText()
                statusList
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Idle,
                        feeds = it.feeds.toMutableList().apply {
                            val items = list.map { statusItem ->
                                val role = statusProvider.statusSourceResolver
                                    .resolveRoleByUri(statusItem.intrinsicBlog.author.uri)
                                MixedContentItemUiState(role, buildStatusUiState(statusItem))
                            }
                            addAllIgnoreDuplicate(items)
                        },
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Failed(e.toTextStringOrNull()),
                    )
                }
            }
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) =
        launchInViewModel {
            val accountUri = status.intrinsicBlog.author.uri
            val role = statusProvider.statusSourceResolver.resolveRoleByUri(accountUri)
            interactiveHandler.onStatusInteractive(role, status, uiInteraction).handleResult()
        }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        launchInViewModel {
            val role = statusProvider.statusSourceResolver.resolveRoleByUri(blogAuthor.uri)
            interactiveHandler.onUserInfoClick(role, blogAuthor).handleResult()
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        launchInViewModel {
            val accountUri = status.intrinsicBlog.author.uri
            val role = statusProvider.statusSourceResolver.resolveRoleByUri(accountUri)
            interactiveHandler.onVoted(role, status, options).handleResult()
        }
    }

    private fun MutableList<MixedContentItemUiState>.addAllIgnoreDuplicate(
        newItems: List<MixedContentItemUiState>,
    ) {
        newItems.forEach {
            this.addIfNotExist(it)
        }
    }

    private fun MutableList<MixedContentItemUiState>.addIfNotExist(newItemUiState: MixedContentItemUiState) {
        if (this.container { it.statusUiState.status.id == newItemUiState.statusUiState.status.id }) return
        this += newItemUiState
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        this.handle(
            messageFlow = _errorMessageFlow,
            openScreenFlow = _openScreenFlow,
            uiStatusUpdater = { newUiState ->
                feedsRepo.updateStatus(newUiState.status)
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
}
