package com.zhangke.utopia.pages.search

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.status.source.StatusSourceOwner

data class SearchUiState(
    val errorMessageText: TextString? = null,
    val searchedData: LoadableState<Pair<StatusSourceOwner, List<StatusSourceUiState>>>,
)
