package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import com.zhangke.utopia.common.status.model.StatusUiState

data class UserTimelineUiState(
    val status: List<StatusUiState>,
    val refreshing: Boolean,
    val loading: Boolean,
)
