package com.zhangke.fread.rss

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.rss.internal.screen.source.RssSourceScreenNavKey
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri

class RssScreenProvider(
    private val uriTransformer: RssUriTransformer,
) : IStatusScreenProvider {

    override fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab? {
        return null
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): NavKey? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?
    ): NavKey? {
        return getUserDetailScreenWithoutAccount(uri)
    }

    override fun getUserDetailScreenWithoutAccount(uri: FormalUri): NavKey? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri) ?: return null
        val url = uriInsight.url
        return RssSourceScreenNavKey(url)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        return null
    }

    override fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        return null
    }
}
