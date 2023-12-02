package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.screen.server.PlatformDetailScreen
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.utopia.activitypub.app.internal.uri.ActivityPubPlatformUri
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import javax.inject.Inject

class ActivityPubScreenProvider @Inject constructor() : IStatusScreenProvider {

    override fun getServerDetailScreen(serverUri: String): Any? {
        val uri = ActivityPubPlatformUri.parse(serverUri) ?: return null
        return PlatformDetailScreen(uri)
    }

    override fun getPostStatusScreen(platform: BlogPlatform): Any? {
        if (platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return PostStatusScreen()
    }
}
