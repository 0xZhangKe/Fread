package com.zhangke.fread.activitypub.app.internal.repo.platform

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ActivityPubPlatformRepo @Inject constructor(
    databases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
    private val activityPubPlatformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubInstanceAdapter: ActivityPubInstanceAdapter,
    private val platformResourceLoader: BlogPlatformResourceLoader,
    private val mastodonInstanceRepo: MastodonInstanceRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    private val localPlatformSnapshotList = mutableListOf<PlatformSnapshot>()

    private val platformDao = databases.getPlatformDao()

    suspend fun getPlatform(locator: PlatformLocator): Result<BlogPlatform> {
        return getPlatform(locator.baseUrl)
    }

    suspend fun getPlatform(baseUrl: FormalBaseUrl): Result<BlogPlatform> {
        return getInstanceInfo(baseUrl).map {
            activityPubInstanceAdapter.toPlatform(baseUrl, it)
        }
    }

    suspend fun getInstanceEntity(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        return getInstanceInfo(baseUrl)
    }

    suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return getAllLocalPlatformSnapshot()
    }

    suspend fun searchPlatformSnapshotFromLocal(query: String): List<PlatformSnapshot> {
        val localPlatforms = getAllLocalPlatformSnapshot()
        return localPlatforms.filter {
            it.domain.contains(query, true) || it.description.contains(query, true)
        }.distinctBy { it.domain }
    }

    suspend fun searchPlatformFromServer(query: String): Result<List<PlatformSnapshot>> {
        return mastodonInstanceRepo.searchWithName(query)
    }

    private suspend fun getAllLocalPlatformSnapshot(): List<PlatformSnapshot> {
        if (localPlatformSnapshotList.isEmpty()) {
            localPlatformSnapshotList += platformResourceLoader.loadLocalPlatforms()
        }
        return localPlatformSnapshotList
    }

    private suspend fun getInstanceInfo(baseUrl: FormalBaseUrl): Result<ActivityPubInstanceEntity> {
        val instanceFromLocal = platformDao.queryByBaseUrl(baseUrl)
        val locator = PlatformLocator(accountUri = null, baseUrl = baseUrl)
        if (instanceFromLocal != null) {
            if (instanceFromLocal.instanceEntity.apiVersions == null) {
                // refresh local data
                val accountUri = loggedAccountProvider.getAllAccounts()
                    .firstOrNull { it.baseUrl == baseUrl }
                    ?.uri
                val fixedLocator = PlatformLocator(accountUri = accountUri, baseUrl = baseUrl)
                ApplicationScope.launch {
                    clientManager.getClient(fixedLocator)
                        .instanceRepo
                        .getInstanceInformation()
                        .map { activityPubPlatformEntityAdapter.toEntity(baseUrl, it) }
                        .onSuccess { platformDao.insert(it) }
                }
            }
            return Result.success(instanceFromLocal.instanceEntity)
        }
        val instanceResult = clientManager.getClient(locator).instanceRepo.getInstanceInformation()
        if (instanceResult.isFailure) {
            return Result.failure(instanceResult.exceptionOrNull()!!)
        }
        val instanceEntity = instanceResult.getOrThrow()
        platformDao.insert(activityPubPlatformEntityAdapter.toEntity(baseUrl, instanceEntity))
        return Result.success(instanceEntity)
    }
}
