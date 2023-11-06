package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feedsId: Int,
        feedsName: String,
        platformList: List<UtopiaPlatform>,
        sourceList: List<String>,
        feedsFlow: Flow<List<Status>>,
    ): FeedsPageUiState {
        return FeedsPageUiState(
            feedsId = feedsId,
            name = feedsName,
            platformList = platformList,
            sourceList = sourceList,
            refreshing = false,
            loading = false,
            feedsFlow = feedsFlow,
            loadMoreError = false,
            snackMessage = null,
        )
    }
}
