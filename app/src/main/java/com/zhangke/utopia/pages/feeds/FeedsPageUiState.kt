package com.zhangke.utopia.pages.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow

data class FeedsPageUiState(
    val name: String,
    val sourceList: List<String>,
    val feedsFlow: Flow<List<Status>>,
    val refreshing: Boolean,
    val loading: Boolean,
    val loadMoreError: Boolean,
    val snackMessage: TextString?,
)
