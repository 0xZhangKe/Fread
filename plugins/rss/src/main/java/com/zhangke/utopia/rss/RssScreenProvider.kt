package com.zhangke.utopia.rss

import com.zhangke.framework.composable.PagerTab
import com.zhangke.utopia.rss.internal.screen.source.RssSourceScreenRoute
import com.zhangke.utopia.rss.internal.uri.RssUriTransformer
import com.zhangke.utopia.rss.internal.uri.isRssUri
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssScreenProvider @Inject constructor(
    private val uriTransformer: RssUriTransformer,
) : IStatusScreenProvider {

    override fun getServerDetailScreenRoute(config: ContentConfig): String? {
        return null
    }

    override fun getPostStatusScreen(platform: BlogPlatform, accountUri: FormalUri?): String? {
        return null
    }

    override fun getReplyBlogScreen(blog: Blog): String? {
        return null
    }

    override fun getAddContentScreenRoute(contentType: ContentType): String? {
        return null
    }

    override fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        return null
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        return null
    }

    override fun getUserDetailRoute(uri: FormalUri): String? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri) ?: return null
        val url = uriInsight.url
        return RssSourceScreenRoute.buildRoute(url)
    }

    override fun getTagTimelineScreenRoute(tag: Hashtag): String? {
        return null
    }
}
