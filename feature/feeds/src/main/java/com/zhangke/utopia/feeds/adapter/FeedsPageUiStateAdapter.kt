package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.feeds.model.Feeds
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feeds: Feeds,
        feedsFlow: Flow<List<Status>>,
    ): FeedsPageUiState {
        return FeedsPageUiState(
            name = feeds.name,
            sourceList = feeds.sourceList,
            refreshing = false,
            loading = false,
            feedsFlow = feedsFlow,
            loadMoreError = false,
            snackMessage = null,
        )
    }
}
