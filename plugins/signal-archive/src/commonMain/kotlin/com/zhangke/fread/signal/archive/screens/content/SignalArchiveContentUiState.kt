package com.zhangke.fread.signal.archive.screens.content

import com.zhangke.fread.signal.archive.model.SignalArchiveIndex

data class SignalArchiveContentUiState(
    val index: SignalArchiveIndex? = null,
    val refreshing: Boolean = false,
    val indexFailed: Boolean = false,
    val selectedTab: Int = 0,
    val generation: Int = 0,
)
