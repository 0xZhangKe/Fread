package com.zhangke.fread.rss

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import me.tatarka.inject.annotations.Inject

class RssNotificationResolver @Inject constructor() : INotificationResolver {

    override suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
        loadedCount: Int,
    ): Result<PagedStatusNotification>? {
        return null
    }
}
