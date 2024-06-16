package com.zhangke.fread.feeds.pages.manager.edit

import com.zhangke.fread.feeds.composable.StatusSourceUiState

internal data class EditMixedContentUiState(
    val name: String,
    val sourceList: List<StatusSourceUiState>,
    val errorMessage: String? = null,
)
