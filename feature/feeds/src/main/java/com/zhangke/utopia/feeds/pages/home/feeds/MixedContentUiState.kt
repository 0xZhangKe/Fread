package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.utopia.common.status.model.StatusUiState

data class MixedContentUiState(
    val feeds: List<StatusUiState>,
    val refreshing: Boolean,
    val loading: Boolean,
    val loadMoreError: Boolean,
) {

    companion object {

        val initialUiState = MixedContentUiState(
            feeds = emptyList(),
            refreshing = false,
            loading = false,
            loadMoreError = false,
        )
    }
}
