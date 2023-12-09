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
            feedsRepo.getStatusFlowByFeedsConfig(config)
                .collect { newFeeds ->
                    mutableState.update {
                        it.copy(
                            loading = false,
                            feeds = newFeeds,
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        screenModelScope.launch {
            mutableState.update {
                it.copy(refreshing = true)
            }
            feedsRepo.fetchStatusByFeedsConfig(config)
                .onSuccess {
                    mutableState.update {
                        it.copy(refreshing = false)
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

    }
}
