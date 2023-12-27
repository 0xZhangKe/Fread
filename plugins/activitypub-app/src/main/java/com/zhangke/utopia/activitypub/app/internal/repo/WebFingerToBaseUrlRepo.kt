package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.WebFingerToBaseUrlEntity
import javax.inject.Inject

class WebFingerToBaseUrlRepo @Inject constructor(
    activityPubDatabases: ActivityPubDatabases,
) {

    private val webFingerToBaseUrlDao = activityPubDatabases.getWebFingerToBaseUrlDao()

    suspend fun queryBaseUrl(webFinger: WebFinger): FormalBaseUrl? {
        return webFingerToBaseUrlDao.queryBaseUrl(webFinger)
    }

    suspend fun insert(webFinger: WebFinger, baseUrl: FormalBaseUrl) {
        webFingerToBaseUrlDao.insert(WebFingerToBaseUrlEntity(webFinger, baseUrl))
    }

    suspend fun delete(entity: WebFingerToBaseUrlEntity) {
        webFingerToBaseUrlDao.delete(entity)
    }
}