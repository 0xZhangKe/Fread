package com.zhangke.utopia.activitypub.app.internal.repo.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.PlatformSnapshot
import javax.inject.Inject

class ActivityPubPlatformRepo @Inject constructor(
    databases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
    private val activityPubPlatformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
    private val resolveBaseUrl: ResolveBaseUrlUseCase,
    private val platformResourceLoader: BlogPlatformResourceLoader,
) {

    private val localPlatformSnapshotList = mutableListOf<PlatformSnapshot>()

    private val platformDao = databases.getPlatformDao()

    suspend fun getPlatform(role: IdentityRole): Result<BlogPlatform> {
        return getPlatform(resolveBaseUrl(role))
    }

    suspend fun getPlatform(baseUrl: FormalBaseUrl): Result<BlogPlatform> {
        return getInstanceInfo(baseUrl).map {
            activityPubInstanceAdapter.toPlatform(baseUrl, it)
        }
    }

    suspend fun getInstanceEntity(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        return getInstanceInfo(baseUrl)
    }

    suspend fun searchPlatformSnapshot(query: String): List<PlatformSnapshot> {
        if (localPlatformSnapshotList.isEmpty()){
            localPlatformSnapshotList += platformResourceLoader.loadLocalPlatforms()
        }
        val localPlatforms = localPlatformSnapshotList
        return localPlatforms.filter {
            it.domain.contains(query, true) || it.description.contains(query, true)
        }.distinctBy { it.domain }
    }

    private suspend fun getInstanceInfo(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        val instanceFromLocal = platformDao.queryByBaseUrl(baseUrl)
        if (instanceFromLocal != null) {
            return Result.success(instanceFromLocal.instanceEntity)
        }
        val role = IdentityRole(accountUri = null, baseUrl = baseUrl)
        val instanceResult = clientManager.getClient(role).instanceRepo.getInstanceInformation()
        if (instanceResult.isFailure) {
            return Result.failure(instanceResult.exceptionOrNull()!!)
        }
        val instanceEntity = instanceResult.getOrThrow()
        platformDao.insert(activityPubPlatformEntityAdapter.toEntity(baseUrl, instanceEntity))
        return Result.success(instanceEntity)
    }
}
