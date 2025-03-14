package com.zhangke.fread.bluesky

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreen
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeTab
import com.zhangke.fread.bluesky.internal.screen.home.edit.BlueskyEditContentScreen
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreen
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.encodeToString
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
        return BlueskyEditContentScreen(content.id)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        uri: FormalUri
    ): Screen? {
        val did = userUriTransformer.parse(uri)?.did ?: return null
        return BskyUserDetailScreen(role = role, did = did)
    }

    override fun getUserDetailRoute(
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

    override fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol
    ): String? {
        TODO("Not yet implemented")
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
        return HomeFeedsScreen(
            role = role,
            feeds = BlueskyFeeds.UserLikes(null),
        )
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
        return null
    }
}
