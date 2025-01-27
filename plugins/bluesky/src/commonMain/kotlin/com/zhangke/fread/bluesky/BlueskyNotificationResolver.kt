package com.zhangke.fread.bluesky

import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.StatusNotification
import me.tatarka.inject.annotations.Inject

class BlueskyNotificationResolver @Inject constructor(): INotificationResolver {

    override suspend fun getNotifications(
        role: IdentityRole,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?
    ): Result<List<StatusNotification>>? {
        TODO("Not yet implemented")
    }
}
