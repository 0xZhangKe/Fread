package com.zhangke.fread.rss

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.rss.internal.screen.source.RssSourceScreenRoute
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class RssScreenProvider @Inject constructor(
    private val uriTransformer: RssUriTransformer,
) : IStatusScreenProvider {

    override suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String? {
        return null
    }

    override suspend fun getEditBlogScreen(role: IdentityRole, blog: Blog): String? {
        return null
    }

    override fun getContentScreen(contentConfig: ContentConfig, isLatestTab: Boolean): PagerTab? {
        return null
    }

    override fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String? {
        return null
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        return null
    }

    override fun getUserDetailRoute(role: IdentityRole, uri: FormalUri): String? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri) ?: return null
        val url = uriInsight.url
        return RssSourceScreenRoute.buildRoute(url)
    }

    override fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): String? {
        return null
    }

    override fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol,
    ): String? {
        return null
    }

    override fun getBlogFavouritedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        return null
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        return null
    }

    override fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        return null
    }

    override fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        return null
    }

    override fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        return null
    }

    override fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl
    ): String? {
        return null
    }

    override fun getBlueskyAddContentScreen(platform: BlogPlatform): String? {
        return null
    }
}
