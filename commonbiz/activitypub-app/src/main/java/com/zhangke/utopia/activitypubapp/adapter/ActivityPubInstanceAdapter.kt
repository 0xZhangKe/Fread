package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.server.StatusProviderServer
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor() {

    fun adapt(
        instance: ActivityPubInstanceEntity,
    ): StatusProviderServer {
        return StatusProviderServer(
            url = ActivityPubUrl.create(instance.domain).toString(),
            name = instance.title,
            description = instance.description,
            thumbnail = instance.thumbnail.url,
        )
    }
}
