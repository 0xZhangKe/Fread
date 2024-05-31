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

    data class FractureItem(val loadState: LoadState) : ActivityPubTimelineItem
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

val List<ActivityPubTimelineItem>.tailFractureCount: Int
    get() {
        var count = 0
        for (i in this.lastIndex downTo 0) {
            if (this[i] is ActivityPubTimelineItem.FractureItem) {
                count++
            } else {
                break
            }
        }
        return count
    }
