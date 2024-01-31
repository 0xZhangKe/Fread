package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.RelationshipSeveranceEvent
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.FormalUri
import java.util.Date

data class ActivityPubNotificationsUiState(
    val notificationList: List<NotificationUiState>,
    val inMentionsTab: Boolean,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val errorMessage: TextString?,
)

data class NotificationUiState(
    val id: String,
    val type: StatusNotificationType,
    val createdAt: Date,
    val displayTime: String,
    /**
     * The account that performed the action that generated the notification.
     */
    val account: ActivityPubAccountEntity,
    val accountUri: FormalUri,
    val status: StatusUiState?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent?,
)
