package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.RegisterApplicationEntry
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubApplication
import javax.inject.Inject

class RegisterApplicationEntryAdapter @Inject constructor() {

    fun toApplication(
        entity: RegisterApplicationEntry,
        baseUrl: FormalBaseUrl,
    ) = ActivityPubApplication(
        baseUrl = baseUrl,
        id = entity.id,
        name = entity.name,
        clientId = entity.clientId,
        clientSecret = entity.clientSecret,
        redirectUri = entity.redirectUri,
        vapidKey = entity.vapidKey,
        website = entity.website,
    )
}
