package com.zhangke.fread.status.notification

import com.zhangke.fread.status.model.IdentityRole

class NotificationResolver(
    private val resolverList: List<INotificationResolver>
) {

    suspend fun getNotifications(
        role: IdentityRole,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?
    ) = resolverList.firstNotNullOfOrNull {
        it.getNotifications(role, type, cursor)
    }
}

interface INotificationResolver {

    enum class NotificationRequestType {
        ALL,
        MENTION,
    }

    suspend fun getNotifications(
        role: IdentityRole,
        type: NotificationRequestType = NotificationRequestType.ALL,
        cursor: String? = null,
    ): Result<List<StatusNotification>>?
}
