package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState

data class UserTimelineUiState(
    val feeds: List<UserTimelineStatus>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
) {

    companion object {

        val default = UserTimelineUiState(
            feeds = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }
}

data class UserTimelineStatus(
    val status: StatusUiState,
    val pinned: Boolean,
)
