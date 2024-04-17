package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.utils.LoadState

data class FollowUiState(
    val initializing: Boolean,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val list: List<ActivityPubAccountEntity>,
)