package com.zhangke.fread.status.status.model

import com.zhangke.fread.status.model.StatusUiState

data class StatusContext(
    val ancestors: List<StatusUiState>,
    val status: StatusUiState?,
    val descendants: List<DescendantStatus>,
)

data class DescendantStatus(
    val status: StatusUiState,
    val descendantStatus: DescendantStatus?,
)
