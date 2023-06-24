package com.zhangke.utopia.pages.sources.add

import com.zhangke.utopia.pages.feeds.shared.source.StatusSourceUiState

data class AddSourceUiState(
    val sourceList: List<StatusSourceUiState>,
    val sourceName: String,
    val showChooseSourceDialog: Boolean,
    val invalidateSourceList: List<StatusSourceUiState>,
)
