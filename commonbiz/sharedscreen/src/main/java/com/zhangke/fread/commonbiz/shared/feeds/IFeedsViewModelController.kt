package com.zhangke.fread.commonbiz.shared.feeds

import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface IFeedsViewModelController : IInteractiveHandler {

    val mutableUiState: MutableStateFlow<CommonFeedsUiState>
    val uiState: StateFlow<CommonFeedsUiState> get() = mutableUiState
    val mutableNewStatusNotifyFlow: MutableSharedFlow<Unit>
    val newStatusNotifyFlow: SharedFlow<Unit> get() = mutableNewStatusNotifyFlow

    fun initController(
        coroutineScope: CoroutineScope,
        roleResolver: (Status) -> IdentityRole,
        loadFirstPageLocalFeeds: suspend () -> Result<List<Status>>,
        loadNewFromServerFunction: suspend () -> Result<RefreshResult>,
        loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>,
        onStatusUpdate: suspend (Status) -> Unit,
    )

    fun initFeeds(needLocalData: Boolean)

    fun startAutoFetchNewerFeeds()

    fun onRefresh()

    fun onLoadMore()
}
