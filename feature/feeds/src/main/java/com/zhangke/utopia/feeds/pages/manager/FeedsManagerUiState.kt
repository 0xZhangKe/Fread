package com.zhangke.utopia.feeds.pages.manager

import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class FeedsManagerUiState(
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
    val showChooseSourceDialog: Boolean,
    val invalidateSourceList: List<StatusSourceUiState>,
)
