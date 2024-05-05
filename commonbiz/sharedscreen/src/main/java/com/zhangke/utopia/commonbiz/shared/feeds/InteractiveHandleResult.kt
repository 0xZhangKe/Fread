package com.zhangke.utopia.commonbiz.shared.feeds

import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface InteractiveHandleResult {

    data class UpdateStatus(val status: StatusUiState) : InteractiveHandleResult

    data class UpdateFollowState(
        val userUri: FormalUri,
        val following: Boolean,
    ) : InteractiveHandleResult
}

suspend fun InteractiveHandleResult.handle(
    uiStatusUpdater: suspend (StatusUiState) -> Unit,
    followStateUpdater: suspend (FormalUri, Boolean) -> Unit,
) {
    when (this) {
        is InteractiveHandleResult.UpdateStatus -> {
            uiStatusUpdater(this.status)
        }

        is InteractiveHandleResult.UpdateFollowState -> {
            val (userUri, following) = this
            followStateUpdater(userUri, following)
        }
    }
}

suspend fun InteractiveHandleResult.handle(
    mutableUiState: MutableStateFlow<CommonLoadableUiState<StatusUiState>>,
    followStateUpdater: suspend (FormalUri, Boolean) -> Unit,
) {
    handle(
        uiStatusUpdater = { newStatus ->
            mutableUiState.update {
                it.copyObject(
                    dataList = it.dataList.updateStatus(newStatus)
                )
            }
        },
        followStateUpdater = followStateUpdater,
    )
}
