package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
) {

    fun toPlatform(
        instance: ActivityPubInstanceEntity
    ): BlogPlatform {
        val baseUrl = FormalBaseUrl.parse(instance.domain)!!
        val uri = platformUriTransformer.build(baseUrl)
        return BlogPlatform(
            uri = uri.toString(),
            baseUrl = baseUrl.toString(),
            name = instance.title,
            description = instance.description,
            protocol = ACTIVITY_PUB_PROTOCOL,
            thumbnail = instance.thumbnail.url,
        )
    }
}
