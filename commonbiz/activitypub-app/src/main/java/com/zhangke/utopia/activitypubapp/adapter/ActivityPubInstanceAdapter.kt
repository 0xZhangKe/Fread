package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status.platform.UtopiaPlatform
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor() {

    fun createPlatform(
        instance: ActivityPubInstance,
    ): UtopiaPlatform {
        return UtopiaPlatform(
            uri = ActivityPubUrl.create(instance.domain).toString(),
            name = instance.title,
            description = instance.description,
            thumbnail = instance.thumbnail.url,
        )
    }
}
