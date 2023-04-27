package com.zhangke.utopia.pages.sources.maintainer

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.status.source.StatusSourceMaintainer

data class SourceMaintainerUiState(
    val title: String,
    val errorMessageText: TextString?,
    val maintainerState: LoadableState<StatusSourceMaintainer>,
)
