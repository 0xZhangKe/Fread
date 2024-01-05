package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.screen.add.AddInstanceScreen
import com.zhangke.utopia.activitypub.app.internal.screen.server.PlatformDetailScreen
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.StatusProviderType
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
) : IStatusScreenProvider {

    override fun getServerDetailScreen(platformUri: FormalUri): Any? {
        val platformUriData = platformUriTransformer.parse(platformUri) ?: return null
        return PlatformDetailScreen(platformUriData.serverBaseUrl)
    }

    override fun getPostStatusScreen(platform: BlogPlatform): Any? {
        if (platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return PostStatusScreen()
    }

    override fun getReplyBlogScreen(blog: Blog): Any? {
        if (blog.platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return PostStatusScreen(replyToBlog = blog)
    }

    override fun getAddContentScreen(statusProviderType: StatusProviderType): Any? {
        if (statusProviderType != StatusProviderType.ACTIVITY_PUB) return null
        return AddInstanceScreen()
    }
}
