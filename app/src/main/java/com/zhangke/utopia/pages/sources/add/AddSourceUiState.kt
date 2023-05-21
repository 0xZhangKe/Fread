package com.zhangke.utopia.pages.sources.add

import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.pages.sources.search.StatusOwnerAndSourceUiState

data class AddSourceUiState(
    val sourceList: List<StatusOwnerAndSourceUiState>,
    val errorMessageText: TextString? = null,
)
