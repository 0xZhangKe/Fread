package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entry.ActivityPubInstanceEntity
import com.zhangke.utopia.activitypub.app.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.platform.UtopiaPlatform
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor() {

    fun toPlatform(
        instance: ActivityPubInstanceEntity
    ): UtopiaPlatform {
        return UtopiaPlatform(
            baseUrl = instance.domain.toBaseUrl(),
            name = instance.title,
            description = instance.description,
            protocol = ACTIVITY_PUB_PROTOCOL,
            thumbnail = instance.thumbnail.url,
        )
    }
}
