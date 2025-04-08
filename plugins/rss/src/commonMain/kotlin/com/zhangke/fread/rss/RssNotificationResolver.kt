package com.zhangke.fread.rss

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import me.tatarka.inject.annotations.Inject

class RssNotificationResolver @Inject constructor() : INotificationResolver {

    override suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?
    ): Result<PagedStatusNotification>? {
        return null
    }

    override suspend fun rejectFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        return null
    }

    override suspend fun acceptFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        return null
    }

    override suspend fun updateUnreadNotification(
        account: LoggedAccount,
        notificationLastReadId: String
    ): Result<Unit>? {
        return null
    }
}
