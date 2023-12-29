package com.zhangke.utopia.commonbiz.shared.screen.status.context

import com.zhangke.utopia.status.status.model.Status

data class StatusContextUiState(
    val anchorStatus: Status,
    val ancestors: List<Status>,
    val descendants: List<Status>,
)
