package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.status.author.BlogAuthor

data class FollowUiState(
    val initializing: Boolean,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val list: List<BlogAuthor>,
)