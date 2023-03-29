package com.zhangke.utopia.pages.feeds

import com.zhangke.framework.composable.LoadableState

data class FeedsContainerUiState(
    val tabIndex: Int,
    val channelList: LoadableState<List<FeedsPageUiState>>,
)