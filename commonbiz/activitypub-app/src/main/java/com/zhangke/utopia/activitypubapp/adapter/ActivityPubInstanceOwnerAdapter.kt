package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject

class ActivityPubInstanceOwnerAdapter @Inject constructor() {

    fun adapt(instance: ActivityPubInstance): StatusSourceOwner {
        return StatusSourceOwner(
            uri = instance.domain,
            name = instance.title,
            description = instance.description,
            thumbnail = instance.thumbnail.url,
        )
    }
}