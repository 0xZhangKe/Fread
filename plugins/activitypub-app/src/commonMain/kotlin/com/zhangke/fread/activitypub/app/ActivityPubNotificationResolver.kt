package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.entities.ActivityPubNotificationsEntity
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.notification.INotificationResolver
import com.zhangke.fread.status.notification.PagedStatusNotification
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject

class ActivityPubNotificationResolver @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
) : INotificationResolver {

    override suspend fun getNotifications(
        account: LoggedAccount,
        type: INotificationResolver.NotificationRequestType,
        cursor: String?,
    ): Result<PagedStatusNotification>? {
        if (account.platform.protocol.notActivityPub) return null
        val isFirstPage = cursor == null
        val locator = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        val platform = platformRepo.getPlatform(locator).let {
            if (it.isFailure) return Result.failure(it.exceptionOrNull()!!)
            it.getOrThrow()
        }
        val loggedAccount = loggedAccountProvider.getAccount(locator)
        val notificationsRepo = clientManager.getClient(locator).notificationsRepo
        val types = mutableListOf<String>()
        if (type == INotificationResolver.NotificationRequestType.MENTION) {
            types += ActivityPubNotificationsEntity.Type.mention
        }

        val (unreadCountResult, notificationsResult) = supervisorScope {
            val notificationDeferred = async {
                notificationsRepo.getNotifications(
                    limit = 50,
                    types = types,
                    maxId = cursor,
                )
            }
            val unreadCountDeferred = async {
                if (isFirstPage) {
                    notificationsRepo.getUnreadNotificationCount(limit = 1000).getOrNull()?.count
                        ?: 0
                } else {
                    0
                }
            }
            val notifications = notificationDeferred.await()
            val unreadCount = unreadCountDeferred.await()
            unreadCount to notifications
        }
        return notificationsResult.map { notifications ->
            val cursor = notifications.lastOrNull()?.id
            PagedStatusNotification(
                cursor = cursor,
                reachEnd = cursor == null,
                notifications = notifications.mapIndexed { index, entity ->
                    val unread = if (isFirstPage) {
                        index < unreadCountResult
                    } else {
                        false
                    }
                    convertNotification(
                        locator = locator,
                        entity = entity,
                        unread = unread,
                        loggedAccount = loggedAccount,
                        platform = platform,
                    )
                }
            )
        }
    }

    override suspend fun getNotificationUserDetail(
        account: LoggedAccount,
        users: List<BlogAuthor>,
    ): Result<List<BlogAuthor>>? {
        if (account !is ActivityPubLoggedAccount) return null
        val userIdList = users.mapNotNull {
            if (it.relationships == null) it.userId else null
        }
        if (userIdList.isEmpty()) return Result.success(emptyList())
        return clientManager.getClient(account.locator)
            .accountRepo
            .getRelationships(idList = userIdList)
            .map { list ->
                users.mapNotNull { user ->
                    val relationship = list.firstOrNull { it.id == user.userId }
                        ?.let { accountAdapter.convertRelationship(it) }
                    if (relationship != null) {
                        user.copy(relationships = relationship)
                    } else {
                        null
                    }
                }
            }
    }

    private suspend fun convertNotification(
        locator: PlatformLocator,
        entity: ActivityPubNotificationsEntity,
        loggedAccount: ActivityPubLoggedAccount?,
        platform: BlogPlatform,
        unread: Boolean,
    ): StatusNotification {
        val createAt = DateParser.parseOrCurrent(entity.createdAt)
        val author = accountAdapter.toAuthor(entity.account)
        val status = entity.status?.let {
            statusAdapter.toStatusUiState(
                entity = it,
                platform = platform,
                locator = locator,
                loggedAccount = loggedAccount,
            )
        }
        return when (entity.type) {
            ActivityPubNotificationsEntity.Type.favourite -> {
                if (status == null) {
                    StatusNotification.Unknown(
                        id = entity.id,
                        createAt = createAt,
                        unread = unread,
                        locator = locator,
                        message = "Unknown notification type: ${entity.type}",
                    )
                } else {
                    StatusNotification.Like(
                        id = entity.id,
                        author = author,
                        locator = locator,
                        blog = status.status.intrinsicBlog,
                        createAt = createAt,
                        unread = unread,
                    )
                }
            }

            ActivityPubNotificationsEntity.Type.mention -> {
                StatusNotification.Mention(
                    id = entity.id,
                    author = author,
                    status = status!!,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.follow -> {
                StatusNotification.Follow(
                    id = entity.id,
                    author = author,
                    locator = locator,
                    createAt = createAt,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.reblog -> {
                StatusNotification.Repost(
                    id = entity.id,
                    author = author,
                    locator = locator,
                    createAt = createAt,
                    blog = status!!.status.intrinsicBlog,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.status -> {
                if (status == null) {
                    StatusNotification.Unknown(
                        id = entity.id,
                        createAt = createAt,
                        unread = unread,
                        locator = locator,
                        message = "Unknown notification type: ${entity.type}",
                    )
                } else {
                    StatusNotification.NewStatus(
                        status = status,
                        unread = unread,
                    )
                }
            }

            ActivityPubNotificationsEntity.Type.followRequest -> {
                StatusNotification.FollowRequest(
                    id = entity.id,
                    createAt = createAt,
                    locator = locator,
                    author = author,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.poll -> {
                if (status == null) {
                    StatusNotification.Unknown(
                        id = entity.id,
                        createAt = createAt,
                        unread = unread,
                        locator = locator,
                        message = "Unknown notification type: ${entity.type}",
                    )
                } else {
                    StatusNotification.Poll(
                        id = entity.id,
                        createAt = createAt,
                        locator = locator,
                        unread = unread,
                        blog = status.status.intrinsicBlog,
                    )
                }
            }

            ActivityPubNotificationsEntity.Type.update -> {
                StatusNotification.Update(
                    id = entity.id,
                    createAt = createAt,
                    status = status!!,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.severedRelationships -> {
                StatusNotification.SeveredRelationships(
                    id = entity.id,
                    createAt = createAt,
                    locator = locator,
                    author = author,
                    unread = unread,
                    reason = entity.relationshipSeveranceEvent?.targetName.ifNullOrEmpty { "Unknown" },
                )
            }

            else -> StatusNotification.Unknown(
                id = entity.id,
                createAt = createAt,
                unread = unread,
                locator = locator,
                message = "Unknown notification type: ${entity.type}",
            )
        }
    }

    override suspend fun rejectFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        if (account !is ActivityPubLoggedAccount) return null
        val userId = requestAuthor.userId
        if (userId.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("Request author userId is empty"))
        }
        val role = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .accountRepo
            .rejectFollowRequest(userId)
            .map { }
    }

    override suspend fun acceptFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        if (account !is ActivityPubLoggedAccount) return null
        val userId = requestAuthor.userId
        if (userId.isNullOrEmpty()) {
            return Result.failure(IllegalArgumentException("Request author userId is empty"))
        }
        val role = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .accountRepo
            .authorizeFollowRequest(userId)
            .map { }
    }

    override suspend fun updateUnreadNotification(
        account: LoggedAccount,
        notificationLastReadId: String
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        val role = PlatformLocator(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .markerRepo
            .saveMarkers(notificationLastReadId = notificationLastReadId)
            .map { }
    }
}
