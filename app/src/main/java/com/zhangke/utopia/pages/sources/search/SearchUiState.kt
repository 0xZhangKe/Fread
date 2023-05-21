package com.zhangke.utopia.pages.sources.search

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.composable.TextString

data class SearchUiState(
    val errorMessageText: TextString? = null,
    val searchedData: LoadableState<StatusOwnerAndSourceUiState>,
)
