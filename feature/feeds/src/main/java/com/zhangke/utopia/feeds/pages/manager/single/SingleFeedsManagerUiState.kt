package com.zhangke.utopia.feeds.pages.manager.single

import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class SingleFeedsManagerUiState(
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
    val showChooseSourceDialog: Boolean,
    val invalidateSourceList: List<StatusSourceUiState>,
)
