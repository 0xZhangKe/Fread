package com.zhangke.utopia.explore.screens.home.tab

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.explore.usecase.GetExplorerItemUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

class ExplorerFeedsViewModel(
    private val type: ExplorerFeedsTabType,
    private val accountUri: FormalUri,
    private val statusProvider: StatusProvider,
    private val interactiveHandler: InteractiveHandler,
    private val getExplorerItem: GetExplorerItemUseCase,
) : SubViewModel() {

    private val loadController = CommonLoadableController<ExplorerItem>(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<ExplorerItem>> get() = loadController.uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow.asSharedFlow()

    init {
        loadController.initData(
            getDataFromServer = { getExplorerItem(accountUri, type, 0, "") },
            getDataFromLocal = null,
        )
    }

    fun onRefresh() {
        loadController.onRefresh(false) {
            getExplorerItem(accountUri, type, 0, "")
        }
    }

    fun onLoadMore() {
        val dataList = uiState.value.dataList
        if (dataList.isEmpty()) return
        loadController.onLoadMore {
            getExplorerItem(
                accountUri = accountUri,
                type = type,
                offset = loadController.uiState.value.dataList.size,
                sinceId = dataList.last().id,
            )
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        launchInViewModel {
            interactiveHandler.onStatusInteractive(status, uiInteraction)
                .handle(
                    uiStatusUpdater = { newStatusUiState ->
                        loadController.mutableUiState.update {
                            it.copy(dataList = it.dataList.updateStatus(newStatusUiState))
                        }
                    },
                    messageFlow = _errorMessageFlow,
                    openScreenFlow = _openScreenFlow,
                )
        }
    }

    private fun List<ExplorerItem>.updateStatus(newStatus: StatusUiState): List<ExplorerItem> {
        return map { item ->
            if (item is ExplorerItem.ExplorerStatus && item.status.status.id == newStatus.status.id) {
                item.copy(status = newStatus)
            } else {
                item
            }
        }
    }

    private val ExplorerItem.id: String
        get() = when (this) {
            is ExplorerItem.ExplorerUser -> user.uri.toString()
            is ExplorerItem.ExplorerHashtag -> hashtag.name
            is ExplorerItem.ExplorerStatus -> status.status.id
        }
}
