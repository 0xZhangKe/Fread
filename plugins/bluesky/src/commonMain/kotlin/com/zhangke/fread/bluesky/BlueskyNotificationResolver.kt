package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfilesQueryParams
import app.bsky.notification.ListNotificationsQueryParams
import app.bsky.notification.UpdateSeenRequest
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyNotificationAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import sh.christian.ozone.api.Did
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class BlueskyNotificationResolver(
    private val clientManager: BlueskyClientManager,
    private val getCompletedNotification: GetCompletedNotificationUseCase,
    private val notificationAdapter: BlueskyNotificationAdapter,
    private val accountAdapter: BlueskyAccountAdapter,
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
                limit = 25,
            ),
        ).map { paged ->
            PagedStatusNotification(
                cursor = paged.cursor,
                reachEnd = paged.cursor == null,
                notifications = paged.notifications.map {
                    notificationAdapter.convert(it, account.locator, account.platform)
                },
            )
        }
    }

    override suspend fun getNotificationUserDetail(
        account: LoggedAccount,
        users: List<BlogAuthor>,
    ): Result<List<BlogAuthor>>? {
        if (account !is BlueskyLoggedAccount) return null
        val didList = users.filter { it.banner.isNullOrEmpty() }
            .mapNotNull { it.webFinger.did }
            .map { Did(it) }
        if (didList.isEmpty()) return Result.success(emptyList())
        return clientManager.getClient(account.locator)
            .getProfilesCatching(GetProfilesQueryParams(actors = didList.take(25)))
            .map { result ->
                result.profiles.map { profile ->
                    accountAdapter.convertToBlogAuthor(
                        did = profile.did.did,
                        handle = profile.handle.handle,
                        profileViewDetailed = profile,
                    )
                }
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

    @OptIn(ExperimentalTime::class)
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
