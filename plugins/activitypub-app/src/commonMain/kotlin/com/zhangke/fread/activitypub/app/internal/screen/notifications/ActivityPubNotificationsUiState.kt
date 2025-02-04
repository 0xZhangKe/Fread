package com.zhangke.fread.activitypub.app.internal.screen.notifications

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.internal.model.RelationshipSeveranceEvent
import com.zhangke.fread.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.datetime.Instant

data class ActivityPubNotificationsUiState(
    val inMentionsTab: Boolean,
    val role: IdentityRole,
    override val initializing: Boolean,
    override val dataList: List<NotificationUiState>,
    override val refreshing: Boolean,
    override val loadMoreState: LoadState,
    override val errorMessage: TextString?,
) : LoadableUiState<NotificationUiState, ActivityPubNotificationsUiState> {

    override fun copyObject(
        dataList: List<NotificationUiState>,
        initializing: Boolean,
        refreshing: Boolean,
        loadMoreState: LoadState,
        errorMessage: TextString?
    ): ActivityPubNotificationsUiState {
        return copy(
            dataList = dataList,
            initializing = initializing,
            refreshing = refreshing,
            loadMoreState = loadMoreState,
            errorMessage = errorMessage,
        )
    }
}

data class NotificationUiState(
    val id: String,
    val type: StatusNotificationType,
    val fromLocal: Boolean,
    val role: IdentityRole,
    val createdAt: Instant,
    val displayTime: String,
    /**
     * The account that performed the action that generated the notification.
     */
    val account: ActivityPubAccountEntity,
    /**
     * Converted by account
     */
    val author: BlogAuthor,
    val unread: Boolean,
    // Just for UI shown, will changed to false when shown.
    val unreadState: Boolean,
    val status: StatusUiState?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent?,
)
