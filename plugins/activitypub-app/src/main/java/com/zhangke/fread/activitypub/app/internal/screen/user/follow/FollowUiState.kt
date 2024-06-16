package com.zhangke.fread.activitypub.app.internal.screen.user.follow

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.author.BlogAuthor

data class FollowUiState(
    val initializing: Boolean,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val list: List<BlogAuthor>,
)