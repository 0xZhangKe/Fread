package com.zhangke.utopia.rss

import com.zhangke.framework.composable.PagerTab
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssScreenProvider @Inject constructor() : IStatusScreenProvider {

    override fun getServerDetailScreenRoute(config: ContentConfig): String? {
        TODO("Not yet implemented")
    }

    override fun getPostStatusScreen(platform: BlogPlatform, accountUri: FormalUri?): String? {
        TODO("Not yet implemented")
    }

    override fun getReplyBlogScreen(blog: Blog): String? {
        TODO("Not yet implemented")
    }

    override fun getAddContentScreenRoute(contentType: ContentType): String? {
        TODO("Not yet implemented")
    }

    override fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        TODO("Not yet implemented")
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        TODO("Not yet implemented")
    }

    override fun getUserDetailRoute(uri: FormalUri): String? {
        TODO("Not yet implemented")
    }

    override fun getTagTimelineScreenRoute(tag: Hashtag): String? {
        TODO("Not yet implemented")
    }
}