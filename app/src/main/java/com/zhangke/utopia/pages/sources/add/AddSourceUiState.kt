package com.zhangke.utopia.pages.sources.add

import com.zhangke.utopia.composable.Text
import com.zhangke.utopia.composable.source.maintainer.SourceMaintainerUiState

data class AddSourceUiState(
    val pendingAdd: Boolean,
    val searching: Boolean,
    val maintainer: SourceMaintainerUiState?,
    val errorMessageText: Text? = null,
)