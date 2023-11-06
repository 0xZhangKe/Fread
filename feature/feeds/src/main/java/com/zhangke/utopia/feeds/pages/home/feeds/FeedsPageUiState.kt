package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.status.platform.UtopiaPlatform
import com.zhangke.utopia.status.status.Status
import kotlinx.coroutines.flow.Flow

internal data class FeedsPageUiState(
    val feedsId: Int,
    val name: String,
    val platformList: List<UtopiaPlatform>,
    val sourceList: List<String>,
    val feedsFlow: Flow<List<Status>>,
    val refreshing: Boolean,
    val loading: Boolean,
    val loadMoreError: Boolean,
    val snackMessage: TextString?,
)
