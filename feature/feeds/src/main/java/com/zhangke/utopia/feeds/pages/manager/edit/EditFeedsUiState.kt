package com.zhangke.utopia.feeds.pages.manager.edit

import com.zhangke.utopia.feeds.composable.StatusSourceUiState

internal data class EditFeedsUiState(
    val name: String,
    val sourceList: List<StatusSourceUiState>,
    val errorMessage: String? = null,
)
