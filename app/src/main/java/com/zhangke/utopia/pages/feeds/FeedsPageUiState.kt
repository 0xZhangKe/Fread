package com.zhangke.utopia.pages.feeds

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.status.status.Status

data class FeedsPageUiState(
    val name: String,
    val sourceList: List<String>,
    val feeds: LoadableState<List<Status>>,
)