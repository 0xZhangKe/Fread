package com.zhangke.fread.activitypub.app.internal.repo

import com.zhangke.activitypub.entities.ActivityPubNotificationsEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubNotificationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.NotificationsEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.db.notifications.NotificationsDatabase
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.StatusNotification
import com.zhangke.fread.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.fread.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri
import javax.inject.Inject

class NotificationsRepo @Inject constructor(
    notificationsDatabase: NotificationsDatabase,
    private val getBlogPlatform: GetActivityPubPlatformUseCase,
    private val clientManager: ActivityPubClientManager,
    private val notificationsEntityAdapter: NotificationsEntityAdapter,
    private val activityPubNotificationEntityAdapter: ActivityPubNotificationEntityAdapter,
) {

    private val notificationDao = notificationsDatabase.notificationsDao()

    suspend fun getLocalNotifications(
        accountOwnershipUri: FormalUri,
        onlyMentions: Boolean = false,
    ): List<StatusNotification> {
        return notificationDao.query(accountOwnershipUri)
            .filter { if (onlyMentions) it.type == StatusNotificationType.MENTION else true }
            .map(notificationsEntityAdapter::toStatusNotification)
    }

    suspend fun getRemoteNotifications(
        account: ActivityPubLoggedAccount,
        onlyMentions: Boolean = false,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<StatusNotification>> {
        val role = IdentityRole(accountUri = account.uri, null)
        val platformResult = getBlogPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val notificationsRepo = clientManager.getClient(role).notificationsRepo
        val types = mutableListOf<String>()
        if (onlyMentions) {
            types += ActivityPubNotificationsEntity.Type.mention
        }
        return notificationsRepo.getNotifications(
            limit = limit,
            types = types,
        ).map { list ->
            list.map { activityPubNotificationEntityAdapter.toNotification(it, platform) }
        }.onSuccess { list ->
            if (!onlyMentions) {
                replaceLocalNotifications(list, account.uri)
            }
        }
    }

    suspend fun loadMoreNotifications(
        account: ActivityPubLoggedAccount,
        maxId: String,
        onlyMentions: Boolean = false,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<StatusNotification>> {
        val role = IdentityRole(accountUri = account.uri, null)
        val notificationsFromLocal = notificationDao.query(
            accountOwnershipUri = account.uri,
        ).filter {
            if (onlyMentions) it.type == StatusNotificationType.MENTION else true
        }
        val index = notificationsFromLocal.indexOfFirst { it.notificationId == maxId }
        if (index >= 0 && index < notificationsFromLocal.lastIndex) {
            return notificationsFromLocal.subList(index + 1, notificationsFromLocal.size)
                .take(limit)
                .map { notificationsEntityAdapter.toStatusNotification(it) }
                .let { Result.success(it) }
        }
        val types = mutableListOf<String>()
        if (onlyMentions) {
            types += ActivityPubNotificationsEntity.Type.mention
        }
        val platformResult = getBlogPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val notificationsRepo = clientManager.getClient(role).notificationsRepo
        return notificationsRepo.getNotifications(
            limit = limit,
            types = types,
            maxId = maxId,
        ).map { list ->
            list.map {
                activityPubNotificationEntityAdapter.toNotification(
                    entity = it,
                    platform = platform,
                )
            }
        }.onSuccess { list ->
            appendToLocal(list, maxId, account.uri)
        }
    }

    suspend fun updateNotifications(
        notification: StatusNotification,
        accountOwnershipUri: FormalUri,
    ) {
        notificationDao.insert(
            notificationsEntityAdapter.toEntity(notification, accountOwnershipUri)
        )
    }

    suspend fun updateNotificationStatus(
        accountOwnershipUri: FormalUri,
        status: Status,
    ) {
        notificationDao.query(accountOwnershipUri)
            .filter {
                it.status?.id == status.id
            }.map {
                it.copy(status = status)
            }.let {
                notificationDao.insert(it)
            }
    }

    private suspend fun replaceLocalNotifications(
        list: List<StatusNotification>,
        accountOwnershipUri: FormalUri,
    ) {
        notificationDao.deleteByAccountUri(accountOwnershipUri)
        notificationDao.insert(list.map {
            notificationsEntityAdapter.toEntity(
                it,
                accountOwnershipUri
            )
        })
    }

    private suspend fun appendToLocal(
        list: List<StatusNotification>,
        maxId: String,
        accountOwnershipUri: FormalUri,
    ) {
        if (notificationDao.query(maxId) == null) return
        notificationDao.insert(list.map {
            notificationsEntityAdapter.toEntity(
                it,
                accountOwnershipUri
            )
        })
    }
}
