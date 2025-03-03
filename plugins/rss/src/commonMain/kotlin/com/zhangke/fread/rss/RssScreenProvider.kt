package com.zhangke.fread.rss

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.rss.internal.screen.source.RssSourceScreen
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class RssScreenProvider @Inject constructor(
    private val uriTransformer: RssUriTransformer,
) : IStatusScreenProvider {

    override fun getReplyBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return null
    }

    override fun getEditBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return null
    }

    override fun getQuoteBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return null
    }

    override fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab? {
        return null
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): Screen? {
        return null
    }

    override fun getUserDetailScreen(role: IdentityRole, uri: FormalUri): Screen? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri) ?: return null
        val url = uriInsight.url
        return RssSourceScreen(url)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return null
    }

    override fun getUserDetailRoute(
        role: IdentityRole,
        did: String,
        protocol: StatusProviderProtocol
    ): Screen? {
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
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        return null
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        return null
    }

    override fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): Screen? {
        return null
    }

    override fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): Screen? {
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
}
