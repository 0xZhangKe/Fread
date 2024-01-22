package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.utopia.activitypub.app.internal.db.notifications.NotificationsDatabase
import javax.inject.Inject

class NotificationsRepo @Inject constructor(
    notificationsDatabase: NotificationsDatabase,
) {

    private val notificationDao = notificationsDatabase.notificationsDao()


}
