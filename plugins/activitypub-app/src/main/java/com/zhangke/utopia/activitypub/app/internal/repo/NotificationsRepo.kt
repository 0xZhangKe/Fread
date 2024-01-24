package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.activitypub.entities.ActivityPubNotificationsEntity
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubNotificationEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.NotificationsEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.notifications.NotificationsDatabase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.status.uri.FormalUri
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
    ): List<StatusNotification> {
        return notificationDao.query(accountOwnershipUri)
            .map(notificationsEntityAdapter::toStatusNotification)
    }

    suspend fun getRemoteNotifications(
        account: ActivityPubLoggedAccount,
        onlyMentions: Boolean = false,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<StatusNotification>> {
        val platformResult = getBlogPlatform(account.baseUrl)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val notificationsRepo = clientManager.getClient(account.baseUrl).notificationsRepo
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
            replaceLocalNotifications(list, account.uri)
        }
    }

    suspend fun loadMoreNotifications(
        account: ActivityPubLoggedAccount,
        maxId: String,
        onlyMentions: Boolean = false,
        limit: Int = StatusConfigurationDefault.config.loadFromLocalLimit,
    ): Result<List<StatusNotification>> {
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
        val platformResult = getBlogPlatform(account.baseUrl)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val notificationsRepo = clientManager.getClient(account.baseUrl).notificationsRepo
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

    suspend fun updateNotifications(notification: StatusNotification){

    }

    private suspend fun replaceLocalNotifications(
        list: List<StatusNotification>,
        accountOwnershipUri: FormalUri,
    ) {
        notificationDao.deleteByAccountUri(accountOwnershipUri)
        notificationDao.insert(list.map { notificationsEntityAdapter.toEntity(it, accountOwnershipUri) })
    }

    private suspend fun appendToLocal(
        list: List<StatusNotification>,
        maxId: String,
        accountOwnershipUri: FormalUri,
    ) {
        if (notificationDao.query(maxId) == null) return
        notificationDao.insert(list.map { notificationsEntityAdapter.toEntity(it, accountOwnershipUri) })
    }
}
