package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.status.model.Status
import java.util.Date

data class StatusNotification (
    val id: String,
    val type: StatusNotificationType,
    val createdAt: Date,
    /**
     * The account that performed the action that generated the notification.
     */
    val account: LoggedAccount,
    val status: Status?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent,
)
