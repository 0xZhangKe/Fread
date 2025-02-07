package com.zhangke.fread.status.notification

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor

class NotificationResolver(
    private val resolverList: List<INotificationResolver>
) {

    suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
    ): Result<PagedStatusNotification> = resolverList.firstNotNullOf {
        it.getNotifications(account, type, cursor)
    }

    suspend fun rejectFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor,
    ): Result<Unit> {
        return resolverList.firstNotNullOf {
            it.rejectFollowRequest(account, requestAuthor)
        }
    }

    suspend fun acceptFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor,
    ): Result<Unit> {
        return resolverList.firstNotNullOf {
            it.acceptFollowRequest(account, requestAuthor)
        }
    }

    suspend fun updateUnreadNotification(
        account: LoggedAccount,
        notificationLastReadId: String,
    ): Result<Unit> {
        return resolverList.firstNotNullOf {
            it.updateUnreadNotification(account, notificationLastReadId)
        }
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
    ): Result<PagedStatusNotification>?

    suspend fun rejectFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>?

    suspend fun acceptFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor,
    ): Result<Unit>?

    suspend fun updateUnreadNotification(
        account: LoggedAccount,
        notificationLastReadId: String,
    ): Result<Unit>?
}
