package com.zhangke.utopia.pages.feeds.container

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.pages.feeds.FeedsPageUiState

data class FeedsContainerUiState(
    val tabIndex: Int,
    val pageUiStateList: LoadableState<List<FeedsPageUiState>>,
)
