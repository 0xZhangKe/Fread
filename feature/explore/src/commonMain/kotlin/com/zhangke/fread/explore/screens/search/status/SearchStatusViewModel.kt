package com.zhangke.fread.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.updateStatus
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
class SearchStatusViewModel(
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    val locator: PlatformLocator,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

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
        loadStatusController.onRefresh(locator) {
            statusProvider.searchEngine
                .searchStatus(locator, query, null)
        }
    }

    fun onLoadMore(query: String) {
        loadStatusController.onLoadMore(locator) {
            statusProvider.searchEngine.searchStatus(locator, query, it)
        }
    }
}
