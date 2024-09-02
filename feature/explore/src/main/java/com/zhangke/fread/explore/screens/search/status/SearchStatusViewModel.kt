package com.zhangke.fread.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.model.updateStatus
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = SearchStatusViewModel.Factory::class)
class SearchStatusViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    @Assisted val role: IdentityRole,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(role: IdentityRole): SearchStatusViewModel
    }

    private val loadStatusController = LoadableStatusController(
        coroutineScope = viewModelScope,
        buildStatusUiState = buildStatusUiState,
    )

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
