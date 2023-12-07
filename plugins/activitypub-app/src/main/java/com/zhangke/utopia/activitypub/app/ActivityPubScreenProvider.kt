package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.screen.server.PlatformDetailScreen
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
) : IStatusScreenProvider {

    override fun getServerDetailScreen(serverUri: String): Any? {
        val uri = StatusProviderUri.from(serverUri) ?: return null
        val platformUriData = platformUriTransformer.parse(uri) ?: return null
        return PlatformDetailScreen(platformUriData.serverBaseUrl)
    }

    override fun getPostStatusScreen(platform: BlogPlatform): Any? {
        if (platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return PostStatusScreen()
    }
}
