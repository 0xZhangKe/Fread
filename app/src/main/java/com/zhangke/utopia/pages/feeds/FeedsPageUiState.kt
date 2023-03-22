package com.zhangke.utopia.pages.feeds

import com.zhangke.utopia.status_provider.Status

data class FeedsPageUiState(
    val name: String,
    val feeds: List<Status>,
)