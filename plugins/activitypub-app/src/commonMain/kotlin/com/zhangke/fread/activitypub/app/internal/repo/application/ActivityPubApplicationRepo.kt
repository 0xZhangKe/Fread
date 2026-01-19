package com.zhangke.fread.activitypub.app.internal.repo.application

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubApplicationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.RegisterApplicationEntryAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubApplicationsDao
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubApplication
import com.zhangke.fread.activitypub.app.internal.platform.FreadApplicationRegisterInfo
import com.zhangke.fread.status.model.PlatformLocator

class ActivityPubApplicationRepo (
    private val databases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
    private val registerApplicationEntryAdapter: RegisterApplicationEntryAdapter,
    private val applicationEntityAdapter: ActivityPubApplicationEntityAdapter,
) {

    private val applicationsDao: ActivityPubApplicationsDao get() = databases.getApplicationDao()

    suspend fun getApplicationByBaseUrl(baseUrl: FormalBaseUrl): ActivityPubApplication? {
        applicationsDao.queryByBaseUrl(baseUrl)
            ?.let(applicationEntityAdapter::toApplication)
            ?.let { return it }
        val locator = PlatformLocator(accountUri = null, baseUrl = baseUrl)
        val application = clientManager.getClient(locator)
            .appsRepo
            .registerApplication(
                clientName = FreadApplicationRegisterInfo.CLIENT_NAME,
                redirectUris = FreadApplicationRegisterInfo.redirectUris,
                scopes = FreadApplicationRegisterInfo.scopes,
                website = FreadApplicationRegisterInfo.WEBSITE,
            ).map { registerApplicationEntryAdapter.toApplication(it, baseUrl) }
            .getOrNull() ?: return null
        insert(application)
        return application
    }

    suspend fun insert(application: ActivityPubApplication) {
        applicationsDao.insert(applicationEntityAdapter.toEntity(application))
    }

    suspend fun delete(application: ActivityPubApplication) {
        applicationsDao.delete(applicationEntityAdapter.toEntity(application))
    }
}