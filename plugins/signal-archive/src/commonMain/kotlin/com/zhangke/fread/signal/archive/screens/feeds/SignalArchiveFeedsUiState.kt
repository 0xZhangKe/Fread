package com.zhangke.fread.signal.archive.screens.feeds

import com.zhangke.fread.signal.archive.model.SignalArchiveArticleSummary

data class SignalArchiveFeedsUiState(
    val items: List<SignalArchiveArticleSummary> = emptyList(),
    val nextPage: Int = 0,
    val loading: Boolean = false,
    val refreshing: Boolean = false,
    val failed: Boolean = false,
    val indexFailed: Boolean = false,
)
