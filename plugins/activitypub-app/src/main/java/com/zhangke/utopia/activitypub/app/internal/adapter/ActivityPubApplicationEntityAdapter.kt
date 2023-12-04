package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubApplication
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubApplicationEntity
import javax.inject.Inject

class ActivityPubApplicationEntityAdapter @Inject constructor() {

    fun toApplication(entity: ActivityPubApplicationEntity) = ActivityPubApplication(
        baseUrl = entity.baseUrl,
        id = entity.id,
        name = entity.name,
        website = entity.website,
        redirectUri = entity.redirectUri,
        clientId = entity.clientId,
        clientSecret = entity.clientSecret,
        vapidKey = entity.vapidKey,
    )

    fun toEntity(application: ActivityPubApplication) = ActivityPubApplicationEntity(
        baseUrl = application.baseUrl,
        id = application.id,
        name = application.name,
        website = application.website,
        redirectUri = application.redirectUri,
        clientId = application.clientId,
        clientSecret = application.clientSecret,
        vapidKey = application.vapidKey,
    )
}
