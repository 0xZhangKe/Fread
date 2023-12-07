package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feedsId: Long,
        feedsName: String,
        platformList: List<BlogPlatform>,
        sourceList: List<StatusProviderUri>,
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
