package com.zhangke.utopia.activitypub.app.internal.adapter

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.getActivityPubProtocol
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
    @ApplicationContext private val context: Context,
) {

    fun toPlatform(
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
            protocol = getActivityPubProtocol(context),
            thumbnail = instance.thumbnail.url,
        )
    }
}
