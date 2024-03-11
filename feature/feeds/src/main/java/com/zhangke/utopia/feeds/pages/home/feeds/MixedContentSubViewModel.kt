package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
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

    init {
        launchInViewModel {
            clearFeedsWhenAccountChanged()
        }
        launchInViewModel {
            feedsRepo.feedsInfoChangedFlow
                .collect { loadPreviousStatus() }
        }
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            loadPreviousStatus()
        }
    }

    private suspend fun loadPreviousStatus() {
        val sourceList = mixedContent?.sourceUriList ?: return
        _uiState.update {
            it.copy(loading = true)
        }
        feedsRepo.getPreviousStatus(sourceList, limit = 10)
            .map { statusList ->
                statusList.preParseRichText()
                statusList
            }
            .onSuccess { list ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        feeds = list.map(buildStatusUiState::invoke),
                    )
                }
            }.onFailure { e ->
                e.message?.let(::textOf)?.let {
                    _errorMessageFlow.emit(it)
                }
                _uiState.update {
                    it.copy(loading = false)
                }
            }
    }

    fun onRefresh() {
        val uiState = _uiState.value
        if (uiState.refreshing) return
        if (uiState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        launchInViewModel {
            _uiState.update {
                it.copy(refreshing = true)
            }
            feedsRepo.getNewerStatus(
                sourceUriList = sourceList,
                minStatusId = feeds.first().status.id,
            ).map { statusList ->
                statusList.preParseRichText()
                statusList
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        refreshing = false,
                        feeds = list.map(buildStatusUiState::invoke) + feeds,
                    )
                }
            }.onFailure { e ->
                e.message?.let(::textOf)?.let {
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
        if (uiState.refreshing) return
        if (uiState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val sourceList = mixedContent?.sourceUriList ?: return
        launchInViewModel {
            _uiState.update {
                it.copy(loading = true)
            }
            feedsRepo.getPreviousStatus(
                sourceList,
                maxId = feeds.last().status.id,
            ).map { statusList ->
                statusList.preParseRichText()
                statusList
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        feeds = feeds + list.map(buildStatusUiState::invoke),
                    )
                }
            }.onFailure { e ->
                e.message?.let(::textOf)?.let {
                    _errorMessageFlow.emit(it)
                }
                _uiState.update {
                    it.copy(loading = false)
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
                _uiState.emit(
                    _uiState.value.copy(
                        feeds = emptyList(),
                    )
                )
                delay(200)
                loadPreviousStatus()
            }
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
