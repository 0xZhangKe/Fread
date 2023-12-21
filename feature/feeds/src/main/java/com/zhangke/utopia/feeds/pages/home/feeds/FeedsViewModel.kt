package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.FeedsConfig
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
                            feeds = list,
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
//        screenModelScope.launch {
//            mutableState.update {
//                it.copy(refreshing = true)
//            }
//            feedsRepo.fetchStatusByFeedsConfig(config)
//                .onSuccess {
//                    mutableState.update {
//                        it.copy(refreshing = false)
//                    }
//                }.onFailure { e ->
//                    e.message?.let(::textOf)?.let {
//                        _errorMessageFlow.emit(it)
//                    }
//                    mutableState.update {
//                        it.copy(refreshing = false)
//                    }
//                }
//        }
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
            feedsRepo.getPreviousStatus(config, maxId = feeds.last().id)
                .onSuccess { list ->
                    mutableState.update {
                        it.copy(
                            loading = false,
                            feeds = feeds + list,
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
}
