package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.utopia.activitypub.app.internal.db.notifications.NotificationsEntity
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import java.util.Date
import javax.inject.Inject

class NotificationsEntityAdapter @Inject constructor() {

    fun toStatusNotification(entity: NotificationsEntity): StatusNotification {
        return StatusNotification(
            id = entity.notificationId,
            type = entity.type,
            createdAt = Date(entity.createTimestamp),
            account = entity.account,
            status = entity.status,
            relationshipSeveranceEvent = entity.relationshipSeveranceEvent,
        )
    }

    fun toEntity(
        notification: StatusNotification,
        accountOwnershipUri: String,
    ): NotificationsEntity {
        return NotificationsEntity(
            notificationId = notification.id,
            type = notification.type,
            accountOwnershipUri = accountOwnershipUri,
            createTimestamp = notification.createdAt.time,
            account = notification.account,
            status = notification.status,
            relationshipSeveranceEvent = notification.relationshipSeveranceEvent,
        )
    }
}
