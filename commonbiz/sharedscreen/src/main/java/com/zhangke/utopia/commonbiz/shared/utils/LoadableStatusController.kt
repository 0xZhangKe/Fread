package com.zhangke.utopia.commonbiz.shared.utils

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.LoadableController
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoadableStatusController(
    private val coroutineScope: CoroutineScope,
    private val interactiveHandler: InteractiveHandler,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private val loadableController = CommonLoadableController<StatusUiState>(coroutineScope)
    val uiState: StateFlow<LoadableUiState<StatusUiState>> get() = loadableController.uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    fun refresh(
        refreshFunction: suspend () -> Result<List<Status>>,
    ) {
        loadableController.refresh {
            refreshFunction().map { list ->
                list.map { buildStatusUiState(it) }
            }
        }
    }

    fun loadMore(
        loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>,
    ) {
        val latestId = loadableController.uiState.value.dataList.lastOrNull()?.status?.id ?: return
        loadableController.loadMore {
            loadMoreFunction(latestId).map { list ->
                list.map { buildStatusUiState(it) }
            }
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        coroutineScope.launch {
            interactiveHandler.onStatusInteractive(status, uiInteraction).handle()
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            interactiveHandler.onUserInfoClick(blogAuthor).handle()
        }
    }

    private suspend fun InteractiveHandleResult.handle() {
        handle(
            messageFlow = _errorMessageFlow,
            openScreenFlow = _openScreenFlow,
            mutableUiState = loadableController.mutableUiState,
        )
    }
}
