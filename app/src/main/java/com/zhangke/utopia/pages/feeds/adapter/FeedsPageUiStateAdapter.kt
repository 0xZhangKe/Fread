package com.zhangke.utopia.pages.feeds.adapter

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.domain.Feeds
import com.zhangke.utopia.pages.feeds.FeedsPageUiState
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feeds: Feeds,
        feedsFlow: Flow<List<Status>>,
    ): FeedsPageUiState {
        return FeedsPageUiState(
            name = feeds.name,
            sourceList = feeds.sourceList,
            refreshing = true,
            loading = false,
            feedsFlow = feedsFlow,
        )
    }
}
