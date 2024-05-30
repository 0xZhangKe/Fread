package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState

data class ActivityPubTimelineUiState(
    val items: List<ActivityPubTimelineItem>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
) {

    companion object {

        val default = ActivityPubTimelineUiState(
            items = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }
}

sealed interface ActivityPubTimelineItem {

    data class StatusItem(
        val status: StatusUiState,
    ) : ActivityPubTimelineItem

    data object FractureItem : ActivityPubTimelineItem
}
