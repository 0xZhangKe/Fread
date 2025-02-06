package com.zhangke.fread.bluesky

import app.bsky.notification.ListNotificationsQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyNotificationAdapter
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import me.tatarka.inject.annotations.Inject

class BlueskyNotificationResolver @Inject constructor(
    private val getCompletedNotification: GetCompletedNotificationUseCase,
    private val notificationAdapter: BlueskyNotificationAdapter,
) : INotificationResolver {

    override suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
        loadedCount: Int,
    ): Result<PagedStatusNotification>? {
        if (account.platform.protocol.notBluesky) return null
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return getCompletedNotification(
            role = role,
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
                    notificationAdapter.convert(it, role, account.platform)
                },
            )
        }
    }
}
