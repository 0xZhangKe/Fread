package com.zhangke.utopia.explore.screens.search.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = SearchStatusViewModel.Factory::class)
class SearchStatusViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    @Assisted val role: IdentityRole,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
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
