package com.zhangke.fread.commonbiz.shared.utils

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.richtext.preParse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class LoadableStatusController(
    protected val coroutineScope: CoroutineScope,
) {

    private val loadableController = CommonLoadableController<StatusUiState>(
        coroutineScope,
        onPostSnackMessage = {
            coroutineScope.launch {
                mutableErrorMessageFlow.emit(it)
            }
        },
    )

    val mutableUiState = loadableController.mutableUiState
    val uiState = loadableController.uiState

    private val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = mutableErrorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<NavKey>()
    val openScreenFlow: SharedFlow<NavKey> get() = _openScreenFlow

    open fun onRefresh(
        locator: PlatformLocator,
        refreshFunction: suspend () -> Result<List<StatusUiState>>,
    ) {
        loadableController.onRefresh {
            refreshFunction().map { list ->
                list.preParse()
                list
            }
        }
    }

    open fun onLoadMore(
        locator: PlatformLocator,
        loadMoreFunction: suspend (maxId: String) -> Result<List<StatusUiState>>,
    ) {
        val latestId = loadableController.uiState.value.dataList.lastOrNull()?.status?.id ?: return
        loadableController.onLoadMore {
            loadMoreFunction(latestId).map { list ->
                list.preParse()
                list
            }
        }
    }
}
