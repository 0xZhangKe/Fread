package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubPlatformUri
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor() {

    fun toPlatform(
        instance: ActivityPubInstanceEntity
    ): BlogPlatform {
        return BlogPlatform(
            uri = ActivityPubPlatformUri.create(instance.domain).toString(),
            baseUrl = instance.domain.toBaseUrl(),
            name = instance.title,
            description = instance.description,
            protocol = ACTIVITY_PUB_PROTOCOL,
            thumbnail = instance.thumbnail.url,
        )
    }
}
