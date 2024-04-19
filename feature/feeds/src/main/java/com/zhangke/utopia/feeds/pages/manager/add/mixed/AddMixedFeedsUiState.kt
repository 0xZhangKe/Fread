package com.zhangke.utopia.feeds.pages.manager.add.mixed

import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class AddMixedFeedsUiState(
    val maxNameLength: Int,
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
)
