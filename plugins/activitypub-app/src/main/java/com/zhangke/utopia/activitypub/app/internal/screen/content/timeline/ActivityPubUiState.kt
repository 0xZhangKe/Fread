package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.composable.LoadPreviousState

data class ActivityPubTimelineUiState(
    val items: List<ActivityPubTimelineItem>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadPreviousState: LoadPreviousState,
    val loadMoreState: LoadState,
) {

    companion object {

        val default = ActivityPubTimelineUiState(
            items = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadPreviousState = LoadPreviousState.Idle,
            loadMoreState = LoadState.Idle,
        )
    }
}

sealed interface ActivityPubTimelineItem {

    data class StatusItem(
        val status: StatusUiState,
    ) : ActivityPubTimelineItem
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
