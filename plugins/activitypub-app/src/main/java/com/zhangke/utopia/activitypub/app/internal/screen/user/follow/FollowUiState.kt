package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import com.zhangke.framework.utils.LoadState

data class FollowUiState(
    val refreshing: Boolean,
    val loadMoreState: LoadState,
)