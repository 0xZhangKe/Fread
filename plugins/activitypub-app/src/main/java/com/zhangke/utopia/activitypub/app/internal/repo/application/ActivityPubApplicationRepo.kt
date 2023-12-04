package com.zhangke.utopia.activitypub.app.internal.repo.application

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubApplicationEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.RegisterApplicationEntryAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubApplicationsDao
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubApplication
import com.zhangke.utopia.activitypub.app.internal.platform.UtopiaApplicationRegisterInfo
import javax.inject.Inject

class ActivityPubApplicationRepo @Inject constructor(
    private val databases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
    private val registerApplicationEntryAdapter: RegisterApplicationEntryAdapter,
    private val applicationEntityAdapter: ActivityPubApplicationEntityAdapter,
) {

    private val applicationsDao: ActivityPubApplicationsDao get() = databases.getApplicationDao()

    suspend fun getApplicationByBaseUrl(baseUrl: String): ActivityPubApplication? {
        applicationsDao.queryByBaseUrl(baseUrl)
            ?.let(applicationEntityAdapter::toApplication)
            ?.let { return it }
        val application = clientManager.getClient(baseUrl)
            .appsRepo
            .registerApplication(
                clientName = UtopiaApplicationRegisterInfo.clientName,
                redirectUris = UtopiaApplicationRegisterInfo.redirectUris,
                scopes = UtopiaApplicationRegisterInfo.scopes,
                website = UtopiaApplicationRegisterInfo.website,
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
