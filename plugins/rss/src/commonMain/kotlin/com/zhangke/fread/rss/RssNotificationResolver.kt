package com.zhangke.fread.rss

import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.StatusNotification
import me.tatarka.inject.annotations.Inject

class RssNotificationResolver @Inject constructor(): INotificationResolver {

    override suspend fun getNotifications(
        role: IdentityRole,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?
    ): Result<List<StatusNotification>>? {
        return null
    }
}
