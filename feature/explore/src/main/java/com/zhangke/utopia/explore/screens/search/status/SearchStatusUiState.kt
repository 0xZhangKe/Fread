package com.zhangke.utopia.explore.screens.search.status

import com.zhangke.utopia.common.status.model.StatusUiState

data class SearchStatusUiState(
    val searching: Boolean,
    val statusList: List<StatusUiState>,
)
