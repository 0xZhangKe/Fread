package com.zhangke.fread.bluesky

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.explorer.ExplorerTab
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPage
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreen
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeTab
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreen
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyScreenProvider @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
) : IStatusScreenProvider {

    override fun getReplyBlogScreen(
        role: IdentityRole,
        blog: Blog
    ): Screen? {
        if (blog.platform.protocol.notBluesky) return null
        return PublishPostScreen(
            role = role,
            replyToJsonString = globalJson.encodeToString(blog),
        )
    }

    override fun getEditBlogScreen(
        role: IdentityRole,
        blog: Blog
    ): Screen? {
        return null
    }

    override fun getQuoteBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        if (blog.platform.protocol.notBluesky) return null
        return PublishPostScreen(
            role = role,
            quoteJsonString = globalJson.encodeToString(blog),
        )
    }

    override fun getContentScreen(
        content: FreadContent,
        isLatestTab: Boolean
    ): PagerTab {
        return BlueskyHomeTab(content.id, isLatestTab)
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): Screen? {
        if (content !is BlueskyContent) return null
        return BskyFollowingFeedsPage(contentId = content.id, role = null)
    }

    override suspend fun getEditContentConfigScreenScreen(account: LoggedAccount): Screen? {
        if (account.platform.protocol.notBluesky) return null
        val role = IdentityRole(baseUrl = account.platform.baseUrl, accountUri = account.uri)
        return BskyFollowingFeedsPage(contentId = null, role = role)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        uri: FormalUri,
        userId: String?,
    ): Screen? {
        val did = userUriTransformer.parse(uri)?.did ?: return null
        return BskyUserDetailScreen(role = role, did = did)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        did: String,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notBluesky) return null
        return BskyUserDetailScreen(role = role, did = did)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notBluesky) return null
        return BskyUserDetailScreen(role, webFinger.did ?: return null)
    }

    override fun getTagTimelineScreen(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notBluesky) return null
        return HomeFeedsScreen.create(
            feeds = BlueskyFeeds.Hashtags(tag),
            role = role,
        )
    }

    override fun getBlogFavouritedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notBluesky) return null
        return UserListScreen(
            role = role,
            type = UserListType.LIKE,
            postUri = blog.url,
        )
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notBluesky) return null
        return UserListScreen(
            role = role,
            type = UserListType.REBLOG,
            postUri = blog.url,
        )
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
        if (protocol.notBluesky) return null
        return HomeFeedsScreen.create(BlueskyFeeds.UserLikes(null), role)
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

    override fun getExplorerTab(role: IdentityRole, platform: BlogPlatform): PagerTab? {
        if (platform.protocol.notBluesky) return null
        return ExplorerTab(role, platform)
    }

    override fun getCreatedListScreen(
        role: IdentityRole,
        platform: BlogPlatform
    ): Screen? {
        return null
    }
}
