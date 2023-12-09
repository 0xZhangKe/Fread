package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import kotlinx.coroutines.flow.Flow

internal data class FeedsPageUiState(
    val feedsId: Long,
    val name: String,
    val platformList: List<BlogPlatform>,
    val sourceList: List<StatusProviderUri>,
    val feedsList: List<Status>,
    val refreshing: Boolean,
    val loading: Boolean,
    val loadMoreError: Boolean,
    val snackMessage: TextString?,
)
