package com.zhangke.fread.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.updateStatus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class SearchStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    @Assisted val role: IdentityRole,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    fun interface Factory : ViewModelFactory {
        fun create(role: IdentityRole): SearchStatusViewModel
    }

    private val loadStatusController = LoadableStatusController(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<StatusUiState>> get() = loadStatusController.uiState

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { result ->
                when (result) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        loadStatusController.mutableUiState.update { state ->
                            state.copy(
                                dataList = state.dataList.updateStatus(result.status),
                            )
                        }
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        loadStatusController.mutableUiState.update { state ->
                            state.copy(
                                dataList = state.dataList.filter { it.status.id != result.statusId },
                            )
                        }
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        // no-op
                    }
                }
            }
        )
    }

    fun initQuery(query: String) {
        if (uiState.value.dataList.isNotEmpty()) return
        onRefresh(query)
    }

    fun onRefresh(query: String) {
        loadStatusController.onRefresh(role) {
            statusProvider.searchEngine
                .searchStatus(role, query, null)
        }
    }

    fun onLoadMore(query: String) {
        loadStatusController.onLoadMore(role) {
            statusProvider.searchEngine.searchStatus(role, query, it)
        }
    }
}
