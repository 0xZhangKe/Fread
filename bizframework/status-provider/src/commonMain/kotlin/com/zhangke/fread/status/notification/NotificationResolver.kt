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

    /**
     * Just return updated user details for the given users.
     */
    suspend fun getNotificationUserDetail(
        account: LoggedAccount,
        users: List<BlogAuthor>,
    ): Result<List<BlogAuthor>> {
        return resolverList.firstNotNullOfOrNull { resolver ->
            resolver.getNotificationUserDetail(account, users)
        } ?: Result.success(emptyList())
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

    suspend fun getNotificationUserDetail(
        account: LoggedAccount,
        users: List<BlogAuthor>,
    ): Result<List<BlogAuthor>>? {
        return null
    }

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
