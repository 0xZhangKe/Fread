package com.zhangke.utopia.feeds.pages.home

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState

internal data class FeedsHomeUiState(
    val tabIndex: Int,
    val pageUiStateList: LoadableState<List<FeedsPageUiState>>,
)

