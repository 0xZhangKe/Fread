package com.zhangke.fread.status.notification

import com.zhangke.fread.status.account.LoggedAccount

class NotificationResolver(
    private val resolverList: List<INotificationResolver>
) {

    suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?
    ): Result<Pair<String?, List<StatusNotification>>> = resolverList.firstNotNullOf {
        it.getNotifications(account, type, cursor)
    }
}

interface INotificationResolver {

    enum class NotificationRequestType {
        ALL,
        MENTION,
    }

    suspend fun getNotifications(
        account: LoggedAccount,
        type: NotificationRequestType = NotificationRequestType.ALL,
        cursor: String? = null,
    ): Result<Pair<String?, List<StatusNotification>>>?
}
