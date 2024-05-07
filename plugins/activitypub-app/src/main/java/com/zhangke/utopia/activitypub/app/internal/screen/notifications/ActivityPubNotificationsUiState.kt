package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.LoadableUiState
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.RelationshipSeveranceEvent
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri
import java.util.Date

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
    val createdAt: Date,
    val displayTime: String,
    /**
     * The account that performed the action that generated the notification.
     */
    val account: ActivityPubAccountEntity,
    /**
     * Converted by account
     */
    val author: BlogAuthor,
    val status: StatusUiState?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent?,
)
