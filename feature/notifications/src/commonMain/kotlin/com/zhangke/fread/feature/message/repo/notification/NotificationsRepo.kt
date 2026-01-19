package com.zhangke.fread.feature.message.repo.notification

import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.uri.FormalUri

class NotificationsRepo(
    database: NotificationsDatabase,
) {

    private val dao = database.notificationsDao()

    suspend fun getNotifications(
        accountUri: FormalUri,
    ): List<StatusNotification> {
        return dao.queryByAccountUri(accountUri).map { it.notification }
    }

    suspend fun replaceNotifications(
        accountUri: FormalUri,
        notifications: List<StatusNotification>,
    ) {
        val entities = notifications.map { it.toEntity(accountUri) }
        dao.delete(accountUri)
        dao.insert(entities)
    }

    suspend fun insertNotification(
        accountUri: FormalUri,
        notifications: List<StatusNotification>,
    ) {
        val entities = notifications.map { it.toEntity(accountUri) }
        dao.insert(entities)
    }

    suspend fun updateNotification(accountUri: FormalUri, notification: StatusNotification) {
        dao.insert(notification.toEntity(accountUri))
    }

    private fun StatusNotification.toEntity(
        accountUri: FormalUri,
    ): NotificationEntity {
        return NotificationEntity(
            notificationId = this.id,
            accountUri = accountUri,
            notification = this,
        )
    }
}
