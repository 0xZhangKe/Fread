package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.internal.uri.server.ActivityPubServerUri
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import com.zhangke.utopia.status.server.StatusProviderServer
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor() {

    fun adapt(
        instance: ActivityPubInstanceEntity,
    ): StatusProviderServer {
        return StatusProviderServer(
            url = ActivityPubUrl.create(instance.domain).toString(),
            uri = ActivityPubServerUri.create(instance.domain).toStatusProviderUri(),
            name = instance.title,
            description = instance.description,
            thumbnail = instance.thumbnail.url,
        )
    }
}
