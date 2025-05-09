package com.zhangke.fread.common.feeds.model

import com.zhangke.fread.status.model.StatusUiState

data class RefreshResult(
    val newStatus: List<StatusUiState>,
    val deletedStatus: List<StatusUiState>,
    val useOldData: Boolean = true,
) {

    companion object {
        val EMPTY = RefreshResult(emptyList(), emptyList())
    }
}
