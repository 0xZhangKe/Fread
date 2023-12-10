package com.zhangke.utopia.activitypub.app.internal.repo.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.usecase.client.GetClientUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubPlatformRepo @Inject constructor(
    databases: ActivityPubDatabases,
    private val getClient: GetClientUseCase,
    private val activityPubPlatformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    private val platformDao = databases.getPlatformDao()

    suspend fun getPlatform(baseUrl: String): Result<BlogPlatform> {
        return getInstanceEntity(baseUrl).map {
            activityPubInstanceAdapter.toPlatform(it)
        }
    }

    suspend fun getInstanceEntity(baseUrl: String): Result<ActivityPubInstanceEntity> {
        return getInstanceInfo(baseUrl)
    }

    private suspend fun getInstanceInfo(baseUrl: String): Result<ActivityPubInstanceEntity> {
        platformDao.queryByBaseUrl(baseUrl)?.let {
            return Result.success(it.instanceEntity)
        }
        val instanceResult = getClient(baseUrl).instanceRepo.getInstanceInformation()
        if (instanceResult.isFailure) {
            return Result.failure(instanceResult.exceptionOrNull()!!)
        }
        val instanceEntity = instanceResult.getOrThrow()
        platformDao.insert(activityPubPlatformEntityAdapter.toEntity(baseUrl, instanceEntity))
        return Result.success(instanceEntity)
    }
}
