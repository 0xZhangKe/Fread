package com.zhangke.fread.feeds.pages.manager.add.mixed

import com.zhangke.fread.feeds.composable.StatusSourceUiState

internal data class AddMixedFeedsUiState(
    val maxNameLength: Int,
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
)
