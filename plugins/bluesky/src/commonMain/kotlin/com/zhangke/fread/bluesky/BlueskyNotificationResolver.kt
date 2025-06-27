package com.zhangke.fread.bluesky

import app.bsky.notification.ListNotificationsQueryParams
import app.bsky.notification.UpdateSeenRequest
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyNotificationAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

class BlueskyNotificationResolver @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getCompletedNotification: GetCompletedNotificationUseCase,
    private val notificationAdapter: BlueskyNotificationAdapter,
) : INotificationResolver {

    override suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
    ): Result<PagedStatusNotification>? {
        if (account !is BlueskyLoggedAccount) return null
        return getCompletedNotification(
            locator = account.locator,
            params = ListNotificationsQueryParams(
                reasons = if (type == INotificationResolver.NotificationRequestType.MENTION) {
                    listOf("mention", "reply", "quote")
                } else {
                    emptyList()
                },
                cursor = cursor,
            ),
        ).map { paged ->
            PagedStatusNotification(
                cursor = paged.cursor,
                notifications = paged.notifications.map {
                    notificationAdapter.convert(it, account.locator, account.platform)
                },
            )
        }
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
        if (account.platform.protocol.notBluesky) return null
        if (account !is BlueskyLoggedAccount) return null
        val client = clientManager.getClient(account.locator)
        return client.updateSeenCatching(UpdateSeenRequest(Clock.System.now()))
            .map { }
    }
}
