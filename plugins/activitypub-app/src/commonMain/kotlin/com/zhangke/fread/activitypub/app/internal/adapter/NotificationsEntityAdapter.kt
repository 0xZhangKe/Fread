package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.fread.activitypub.app.internal.db.notifications.NotificationsEntity
import com.zhangke.fread.activitypub.app.internal.model.StatusNotification
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

class NotificationsEntityAdapter @Inject constructor() {

    fun toStatusNotification(entity: NotificationsEntity): StatusNotification {
        return StatusNotification(
            id = entity.notificationId,
            type = entity.type,
            createdAt = Instant.fromEpochMilliseconds(entity.createTimestamp),
            account = entity.account,
            status = entity.status,
            relationshipSeveranceEvent = entity.relationshipSeveranceEvent,
        )
    }

    fun toEntity(
        notification: StatusNotification,
        accountOwnershipUri: FormalUri,
    ): NotificationsEntity {
        return NotificationsEntity(
            notificationId = notification.id,
            type = notification.type,
            accountOwnershipUri = accountOwnershipUri,
            createTimestamp = notification.createdAt.toEpochMilliseconds(),
            account = notification.account,
            status = notification.status,
            relationshipSeveranceEvent = notification.relationshipSeveranceEvent,
        )
    }
}
