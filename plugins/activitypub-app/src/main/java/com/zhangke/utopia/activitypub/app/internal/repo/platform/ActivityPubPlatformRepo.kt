package com.zhangke.utopia.activitypub.app.internal.repo.platform

import android.util.Log
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubPlatformRepo @Inject constructor(
    databases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
    private val activityPubPlatformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
) {

    private val platformDao = databases.getPlatformDao()

    suspend fun getPlatform(baseUrl: FormalBaseUrl): Result<BlogPlatform> {
        val entity = getInstanceInfo(baseUrl)
        Log.d("U_TEST", "getPlatform $entity")
        return entity.map {
            try {
                activityPubInstanceAdapter.toPlatform(it)
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }.onSuccess {
            Log.d("U_TEST", "onSuccess $it")
        }.onFailure {
            Log.d("U_TEST", "onFailure $it")
        }
    }

    suspend fun getInstanceEntity(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        return getInstanceInfo(baseUrl)
    }

    private suspend fun getInstanceInfo(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        val instanceFromLocal = platformDao.queryByBaseUrl(baseUrl)
        if (instanceFromLocal != null) {
            Log.d("U_TEST", "queryByBaseUrl $instanceFromLocal")
            try {
                return Result.success(instanceFromLocal.instanceEntity)
            }catch (e: Throwable){
                e.printStackTrace()
                Log.d("U_TEST", "queryByBaseUrl catching: $e")
            }
        }
        Log.d("U_TEST", "getInstanceInfo by api")
        val instanceResult = clientManager.getClient(baseUrl).instanceRepo.getInstanceInformation()
        if (instanceResult.isFailure) {
            return Result.failure(instanceResult.exceptionOrNull()!!)
        }
        val instanceEntity = instanceResult.getOrThrow()
        platformDao.insert(activityPubPlatformEntityAdapter.toEntity(baseUrl, instanceEntity))
        return Result.success(instanceEntity)
    }
}
