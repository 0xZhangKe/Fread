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
import com.zhangke.fread.status.model.IdentityRole
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
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        val platform = platformRepo.getPlatform(role).let {
            if (it.isFailure) return Result.failure(it.exceptionOrNull()!!)
            it.getOrThrow()
        }
        val loggedAccount = loggedAccountProvider.getAccount(role)
        val notificationsRepo = clientManager.getClient(role).notificationsRepo
        val types = mutableListOf<String>()
        if (type == INotificationResolver.NotificationRequestType.MENTION) {
            types += ActivityPubNotificationsEntity.Type.mention
        }

        val (unreadCountResult, notificationsResult) = supervisorScope {
            val notificationDeferred = async {
                notificationsRepo.getNotifications(
                    limit = 50,
                    types = types,
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
            PagedStatusNotification(
                cursor = notifications.lastOrNull()?.id,
                notifications = notifications.mapIndexed { index, entity ->
                    val unread = if (isFirstPage) {
                        index < unreadCountResult
                    } else {
                        false
                    }
                    convertNotification(
                        role = role,
                        entity = entity,
                        unread = unread,
                        loggedAccount = loggedAccount,
                        platform = platform,
                    )
                }
            )
        }
    }

    private suspend fun convertNotification(
        role: IdentityRole,
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
                role = role,
                loggedAccount = loggedAccount,
            )
        }
        return when (entity.type) {
            ActivityPubNotificationsEntity.Type.favourite -> {
                StatusNotification.Like(
                    id = entity.id,
                    author = author,
                    role = role,
                    blog = status!!.status.intrinsicBlog,
                    createAt = createAt,
                    unread = unread,
                )
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
                    role = role,
                    createAt = createAt,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.reblog -> {
                StatusNotification.Repost(
                    id = entity.id,
                    author = author,
                    role = role,
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
                        role = role,
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
                    role = role,
                    author = author,
                    unread = unread,
                )
            }

            ActivityPubNotificationsEntity.Type.poll -> {
                StatusNotification.Poll(
                    id = entity.id,
                    createAt = createAt,
                    role = role,
                    unread = unread,
                    blog = status!!.status.intrinsicBlog,
                )
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
                    role = role,
                    author = author,
                    unread = unread,
                    reason = entity.relationshipSeveranceEvent?.targetName.ifNullOrEmpty { "Unknown" },
                )
            }

            else -> StatusNotification.Unknown(
                id = entity.id,
                createAt = createAt,
                unread = unread,
                role = role,
                message = "Unknown notification type: ${entity.type}",
            )
        }
    }

    override suspend fun rejectFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        val activityPubAccount = account as? ActivityPubLoggedAccount ?: return null
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .accountRepo
            .rejectFollowRequest(activityPubAccount.userId)
            .map { }
    }

    override suspend fun acceptFollowRequest(
        account: LoggedAccount,
        requestAuthor: BlogAuthor
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        val activityPubAccount = account as? ActivityPubLoggedAccount ?: return null
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .accountRepo
            .authorizeFollowRequest(activityPubAccount.userId)
            .map { }
    }

    override suspend fun updateUnreadNotification(
        account: LoggedAccount,
        notificationLastReadId: String
    ): Result<Unit>? {
        if (account.platform.protocol.notActivityPub) return null
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return clientManager.getClient(role)
            .markerRepo
            .saveMarkers(notificationLastReadId = notificationLastReadId)
            .map { }
    }
}
