package com.zhangke.utopia.explore.screens.home.tab

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.status.StatusProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

class ExplorerFeedsViewModel(
    private val type: ExplorerFeedsTabType,
    private val statusProvider: StatusProvider,
    private val interactiveHandler: InteractiveHandler,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : SubViewModel() {

    private val loadController = CommonLoadableController<ExplorerItem>(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<ExplorerItem>> get() = loadController.uiState

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow.asSharedFlow()

    init {
//        loadController.initData(
//            getDataFromServer = {
////                statusProvider.searchEngine.searchStatus("", null)
//            }
//        )
    }

    fun onRefresh(query: String) {
    }

    fun onLoadMore(query: String) {
    }
}
