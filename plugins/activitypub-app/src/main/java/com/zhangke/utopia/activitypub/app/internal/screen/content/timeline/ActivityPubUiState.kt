package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.loadable.previous.PreviousPageLoadingState
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
}

fun List<ActivityPubTimelineItem>.updateStatus(
    status: StatusUiState,
): List<ActivityPubTimelineItem> {
    return map {
        if (it is ActivityPubTimelineItem.StatusItem && it.status.status.intrinsicBlog.id == status.status.intrinsicBlog.id) {
            ActivityPubTimelineItem.StatusItem(status)
        } else {
            it
        }
    }
}

fun List<ActivityPubTimelineItem>.getStatusIdOrNull(index: Int): String? {
    return this.getOrNull(index)?.let {
        if (it is ActivityPubTimelineItem.StatusItem) {
            it.status.status.id
        } else {
            null
        }
    }
}
