package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.model.StatusUiState

data class UserTimelineUiState(
    val feeds: List<StatusUiState>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
) {

    companion object {

        fun default() = UserTimelineUiState(
            feeds = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }
}
