package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubNotificationEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.NotificationsEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.db.notifications.NotificationsDatabase
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import javax.inject.Inject

class NotificationsRepo @Inject constructor(
    notificationsDatabase: NotificationsDatabase,
    private val notificationsEntityAdapter: NotificationsEntityAdapter,
    private val activityPubNotificationEntityAdapter: ActivityPubNotificationEntityAdapter,
) {

    private val notificationDao = notificationsDatabase.notificationsDao()

    fun getLocalNotifications(
        accountOwnershipId: String
    ): List<StatusNotification> {
//        notificationDao.getNotifications()
    }
}
