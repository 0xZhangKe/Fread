package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.richtext.preParseRichText
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var mixedContent: ContentConfig.MixedContent? = null

    private val config = StatusConfigurationDefault.config

    init {
        launchInViewModel {
            clearFeedsWhenAccountChanged()
        }
        launchInViewModel {
            feedsRepo.feedsInfoChangedFlow
                .collect {
                    _uiState.update { it.resetState() }
                    initFeeds()
                }
        }
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            initFeeds()
        }
    }

    /**
     * 初始化Feeds
     * 先获取本地，然后获取服务端。
     */
    private fun initFeeds() {
        val sourceList = mixedContent?.sourceUriList ?: return
        launchInViewModel {
            _uiState.value = _uiState.value.copy(
                initializing = true,
                initErrorMessage = null,
            )
            val localStatus = feedsRepo.getLocalFirstPageStatus(
                sourceUriList = sourceList,
                limit = config.loadFromLocalLimit,
            )
            _uiState.value = _uiState.value.copy(
                feeds = localStatus.map(buildStatusUiState::invoke),
            )
            feedsRepo.refresh(
                sourceUriList = sourceList,
                limit = config.loadFromServerLimit,
            ).onFailure {
                _uiState.update { state ->
                    state.copy(
                        initializing = false,
                        initErrorMessage = it.toTextStringOrNull(),
                    )
                }
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        initializing = false,
                        feeds = state.feeds.applyRefreshResult(it),
                    )
                }
            }
        }
    }

    private fun List<StatusUiState>.applyRefreshResult(
        refreshResult: RefreshResult,
    ): List<StatusUiState> {
        val deletedIdsSet = refreshResult.deletedStatus
            .map { it.id }
            .toSet()
        val finalList = this.filter {
            !deletedIdsSet.contains(it.status.id)
        }.toMutableList()
        finalList.addAllIgnoreDuplicate(refreshResult.newStatus.map(buildStatusUiState::invoke))
        return finalList.sortedByDescending { it.status.datetime }
    }

    fun onRefresh() {
        val uiState = _uiState.value
        if (uiState.initializing || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        launchInViewModel {
            _uiState.update {
                it.copy(refreshing = true)
            }
            feedsRepo.refresh(
                sourceUriList = sourceList,
                limit = config.loadFromServerLimit,
            ).onSuccess { refreshResult ->
                _uiState.update {
                    it.copy(
                        refreshing = false,
                        feeds = it.feeds.applyRefreshResult(refreshResult),
                    )
                }
            }.onFailure { e ->
                e.toTextStringOrNull()?.let {
                    _errorMessageFlow.emit(it)
                }
                _uiState.update {
                    it.copy(refreshing = false)
                }
            }
        }
    }

    fun onLoadMore() {
        val uiState = _uiState.value
        if (uiState.initializing || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        launchInViewModel {
            _uiState.update {
                it.copy(loadMoreState = LoadState.Loading)
            }
            feedsRepo.getStatus(
                sourceList,
                limit = config.loadFromServerLimit,
                maxId = feeds.last().status.id,
            ).map { statusList ->
                statusList.preParseRichText()
                statusList
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Loading,
                        feeds = it.feeds.toMutableList().apply {
                            addAllIgnoreDuplicate(list.map(buildStatusUiState::invoke))
                        },
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        loadMoreState = LoadState.Failed(e),
                    )
                }
            }
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) =
        launchInViewModel {
            interactiveHandler.onStatusInteractive(status, uiInteraction).handleResult()
        }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        launchInViewModel {
            interactiveHandler.onUserInfoClick(blogAuthor).handleResult()
        }
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        launchInViewModel { interactiveHandler.onVoted(status, options).handleResult() }
    }

    private suspend fun clearFeedsWhenAccountChanged() {
        statusProvider.accountManager
            .getAllAccountFlow()
            .collect {
                _uiState.update { it.resetState() }
                delay(200)
                initFeeds()
            }
    }

    private fun MixedContentUiState.resetState(): MixedContentUiState {
        return copy(
            feeds = emptyList(),
            initializing = false,
            initErrorMessage = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }

    private fun MutableList<StatusUiState>.addAllIgnoreDuplicate(
        newStatusList: List<StatusUiState>,
    ) {
        newStatusList.forEach { newStatus ->
            this.addIfNotExist(newStatus)
        }
    }

    private fun MutableList<StatusUiState>.addIfNotExist(newStatus: StatusUiState) {
        if (this.container { it.status.id == newStatus.status.id }) return
        this += newStatus
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        this.handle(
            messageFlow = _errorMessageFlow,
            openScreenFlow = _openScreenFlow,
            uiStatusUpdater = { newUiState ->
                feedsRepo.updateStatus(newUiState.status)
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        feeds = currentUiState.feeds.updateStatus(newUiState)
                    )
                }
            }
        )
    }
}
