package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.RegisterApplicationEntry
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubApplication
import javax.inject.Inject

class RegisterApplicationEntryAdapter @Inject constructor() {

    fun toApplication(
        entity: RegisterApplicationEntry,
        baseUrl: String,
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
