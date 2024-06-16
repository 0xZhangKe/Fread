package com.zhangke.fread.commonbiz.shared.feeds

import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.model.updateStatus
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed interface InteractiveHandleResult {

    data class UpdateStatus(val status: StatusUiState) : InteractiveHandleResult

    data class DeleteStatus(val statusId: String) : InteractiveHandleResult

    data class UpdateFollowState(
        val userUri: FormalUri,
        val following: Boolean,
    ) : InteractiveHandleResult
}

suspend fun InteractiveHandleResult.handle(
    uiStatusUpdater: suspend (StatusUiState) -> Unit,
    deleteStatus: (statusId: String) -> Unit,
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

        is InteractiveHandleResult.DeleteStatus -> {
            deleteStatus(this.statusId)
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
        deleteStatus = { deletedStatusId ->
            mutableUiState.update { uiState ->
                uiState.copyObject(
                    dataList = uiState.dataList
                        .filter { status -> status.status.id != deletedStatusId }
                )
            }
        },
        followStateUpdater = followStateUpdater,
    )
}
