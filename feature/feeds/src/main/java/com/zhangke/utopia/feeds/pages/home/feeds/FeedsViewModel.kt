package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.repo.FeedsConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedsViewModel @AssistedInject constructor(
    @Assisted val config: FeedsConfig,
    private val feedsRepo: FeedsRepo,
    private val feedsConfigRepo: FeedsConfigRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : StateScreenModel<FeedsScreenUiState>(FeedsScreenUiState.initialUiState) {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(config: FeedsConfig): FeedsViewModel
    }

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    init {
        screenModelScope.launch {
            mutableState.update {
                it.copy(loading = true)
            }
            feedsRepo.getPreviousStatus(config, maxId = config.lastReadStatusId)
                .onSuccess { list ->
                    mutableState.update {
                        it.copy(
                            loading = false,
                            feeds = list.map(buildStatusUiState::invoke),
                        )
                    }
                }.onFailure { e ->
                    e.message?.let(::textOf)?.let {
                        _errorMessageFlow.emit(it)
                    }
                    mutableState.update {
                        it.copy(loading = false)
                    }

                }
        }
    }

    fun onRefresh() {
        val uiState = mutableState.value
        if (uiState.refreshing) return
        if (uiState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        screenModelScope.launch {
            mutableState.update {
                it.copy(refreshing = true)
            }
            feedsRepo.getNewerStatus(
                feedsConfig = config,
                minStatusId = feeds.first().status.id,
            ).onSuccess { list ->
                mutableState.update {
                    it.copy(
                        refreshing = false,
                        feeds = list.map(buildStatusUiState::invoke) + feeds,
                    )
                }
            }.onFailure { e ->
                e.message?.let(::textOf)?.let {
                    _errorMessageFlow.emit(it)
                }
                mutableState.update {
                    it.copy(refreshing = false)
                }
            }
        }
    }

    fun onLoadMore() {
        val uiState = mutableState.value
        if (uiState.refreshing) return
        if (uiState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        screenModelScope.launch {
            mutableState.update {
                it.copy(loading = true)
            }
            feedsRepo.getPreviousStatus(config, maxId = feeds.last().status.id)
                .onSuccess { list ->
                    mutableState.update {
                        it.copy(
                            loading = false,
                            feeds = feeds + list.map(buildStatusUiState::invoke),
                        )
                    }
                }.onFailure { e ->
                    e.message?.let(::textOf)?.let {
                        _errorMessageFlow.emit(it)
                    }
                    mutableState.update {
                        it.copy(loading = false)
                    }
                }
        }
    }

    fun onCatchMinFirstVisibleIndex(index: Int) {
        val uiState = mutableState.value
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        val fixedIndex = index.coerceAtLeast(0).coerceAtMost(feeds.lastIndex)
        screenModelScope.launch {
            feedsConfigRepo.updateLastReadStatusId(
                feedsConfig = config,
                lastReadStatusId = feeds[fixedIndex].status.id,
            )
        }
    }

    fun onInteractive(interaction: StatusUiInteraction) {
    }
}
