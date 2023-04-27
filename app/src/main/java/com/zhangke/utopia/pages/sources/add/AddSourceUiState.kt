package com.zhangke.utopia.pages.sources.add

import com.zhangke.utopia.composable.TextString

data class AddSourceUiState(
    val pendingAdd: Boolean,
    val searching: Boolean,
    val maintainer: SourceMaintainerUiState?,
    val errorMessageText: TextString? = null,
)
