package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.status.status.model.Status
import java.util.Date

data class StatusNotification(
    val id: String,
    val type: StatusNotificationType,
    val createdAt: Date,
    /**
     * The account that performed the action that generated the notification.
     */
    val account: ActivityPubAccountEntity,
    val status: Status?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent?,
)
