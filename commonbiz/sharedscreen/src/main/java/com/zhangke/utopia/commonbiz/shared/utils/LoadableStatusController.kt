package com.zhangke.utopia.commonbiz.shared.utils

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.richtext.preParseRichText
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

open class LoadableStatusController(
    protected val coroutineScope: CoroutineScope,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private val loadableController = CommonLoadableController<StatusUiState>(coroutineScope)

    val mutableUiState = loadableController.mutableUiState
    val uiState = loadableController.uiState

    private val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = mutableErrorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    open fun onRefresh(
        role: IdentityRole,
        refreshFunction: suspend () -> Result<List<Status>>,
    ) {
        loadableController.onRefresh {
            refreshFunction().map { list ->
                list.preParseRichText()
                list.map { buildStatusUiState(role, it) }
            }
        }
    }

    open fun onLoadMore(
        role: IdentityRole,
        loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>,
    ) {
        val latestId = loadableController.uiState.value.dataList.lastOrNull()?.status?.id ?: return
        loadableController.onLoadMore {
            loadMoreFunction(latestId).map { list ->
                list.preParseRichText()
                list.map { buildStatusUiState(role, it) }
            }
        }
    }
}
