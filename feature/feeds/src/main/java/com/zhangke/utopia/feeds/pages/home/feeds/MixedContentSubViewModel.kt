package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
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
) : SubViewModel() {

    private val _uiState = MutableStateFlow(MixedContentUiState.initialUiState)
    val uiState: StateFlow<MixedContentUiState> get() = _uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    private var mixedContent: ContentConfig.MixedContent? = null

    init {
        launchInViewModel {
            clearFeedsWhenAccountChanged()
        }
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            loadPreviousStatus()
        }
    }

    private suspend fun loadPreviousStatus() {
        val sourceList = mixedContent?.sourceUriList ?: return
        val lastReadStatusId = mixedContent?.lastReadStatusId
        _uiState.update {
            it.copy(loading = true)
        }
        feedsRepo.getPreviousStatus(
            sourceList,
            maxId = lastReadStatusId,
        ).onSuccess { list ->
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
            ).onSuccess { list ->
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
            ).onSuccess { list ->
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

    fun onCatchMinFirstVisibleIndex(index: Int) {
        val uiState = _uiState.value
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val fixedIndex = index.coerceAtLeast(0).coerceAtMost(feeds.lastIndex)
        launchInViewModel {
            contentConfigRepo.updateLatestStatusId(
                id = configId,
                latestStatusId = feeds[fixedIndex].status.id,
            )
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) =
        launchInViewModel {
            if (uiInteraction is StatusUiInteraction.Comment) {
                statusProvider.screenProvider
                    .getReplyBlogScreen(status.intrinsicBlog)
                    ?.let {
                        _openScreenFlow.emit(it)
                    }
                return@launchInViewModel
            }
            val interaction = uiInteraction.statusInteraction ?: return@launchInViewModel
            statusProvider.statusResolver
                .interactive(status, interaction)
                .onSuccess { newStatus ->
                    feedsRepo.updateStatus(newStatus)
                    val currentValue = _uiState.value
                    _uiState.value = currentValue.copy(
                        feeds = currentValue.feeds
                            .map { state ->
                                if (state.status.id == newStatus.id) {
                                    buildStatusUiState(newStatus)
                                } else {
                                    state
                                }
                            }
                    )
                }.onFailure {
                    it.message?.takeIf { it.isNotEmpty() }
                        ?.let { message ->
                            _errorMessageFlow.emit(textOf(message))
                        }
                }
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
}
