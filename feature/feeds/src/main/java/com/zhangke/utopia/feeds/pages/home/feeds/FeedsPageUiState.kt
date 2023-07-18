package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow

internal data class FeedsPageUiState(
    val name: String,
    val sourceList: List<StatusSource>,
    val feedsFlow: Flow<List<Status>>,
    val refreshing: Boolean,
    val loading: Boolean,
    val loadMoreError: Boolean,
    val snackMessage: TextString?,
)
