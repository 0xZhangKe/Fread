package com.zhangke.utopia.pages.feeds

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.status.Status

data class FeedsPageUiState(
    val name: String,
    val feeds: LoadableState<List<Status>>,
)