package com.zhangke.utopia.feeds.pages.manager.add

import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class AddFeedsManagerUiState(
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
)
