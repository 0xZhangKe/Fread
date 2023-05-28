package com.zhangke.utopia.pages.sources.add

import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.status.source.StatusSource

data class AddSourceUiState(
    val sourceList: List<StatusSourceUiState>,
    val errorMessageText: TextString? = null,
)
