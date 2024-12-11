package com.zhangke.fread.bluesky

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentRoute
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeTab
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyScreenProvider @Inject constructor() : IStatusScreenProvider {

    override suspend fun getReplyBlogScreen(
        role: IdentityRole,
        blog: Blog
    ): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getEditBlogScreen(
        role: IdentityRole,
        blog: Blog
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getContentScreen(
        contentConfig: ContentConfig,
        isLatestTab: Boolean
    ): PagerTab? {
        return BlueskyHomeTab(contentConfig.id, isLatestTab)
    }

    override fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String? {
        TODO("Not yet implemented")
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        TODO("Not yet implemented")
    }

    override fun getUserDetailRoute(
        role: IdentityRole,
        uri: FormalUri
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getBlogFavouritedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl
    ): String? {
        TODO("Not yet implemented")
    }

    override fun getBlueskyAddContentScreen(platform: BlogPlatform): String? {
        return AddBlueskyContentRoute.buildRoute(platform.baseUrl)
    }
}
