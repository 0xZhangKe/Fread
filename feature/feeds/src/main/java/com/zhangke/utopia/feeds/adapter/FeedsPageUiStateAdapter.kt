package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.feeds.model.Feeds
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feedsName: String,
        sourceList: List<StatusSource>,
        feedsFlow: Flow<List<Status>>,
    ): FeedsPageUiState {
        return FeedsPageUiState(
            name = feedsName,
            sourceList = sourceList,
            refreshing = false,
            loading = false,
            feedsFlow = feedsFlow,
            loadMoreError = false,
            snackMessage = null,
        )
    }
}
