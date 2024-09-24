package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ActivityPubInstanceAdapter @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
) {

    suspend fun toPlatform(
        baseUrl: FormalBaseUrl,
        instance: ActivityPubInstanceEntity
    ): BlogPlatform {
        // 此处需要注意的是，ActivityPubInstanceEntity#domain 并不一定准确。
        // 例如 https://mastodon.jakewharton.com/api/v2/instance
        // 他的 domain 是 jakewharton.com，而不是 mastodon.jakewharton.com
        val uri = platformUriTransformer.build(baseUrl)
        return BlogPlatform(
            uri = uri.toString(),
            baseUrl = baseUrl,
            name = instance.title,
            description = instance.description,
            protocol = createActivityPubProtocol(),
            thumbnail = instance.thumbnail.url,
        )
    }
}
