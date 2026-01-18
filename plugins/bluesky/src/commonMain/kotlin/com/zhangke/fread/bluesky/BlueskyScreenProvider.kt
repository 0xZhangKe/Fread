package com.zhangke.fread.bluesky

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.explorer.ExplorerTab
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPageNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeTab
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
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
        locator: PlatformLocator,
        blog: Blog
    ): NavKey? {
        if (blog.platform.protocol.notBluesky) return null
        return null
    }

    override fun getEditBlogScreen(
        locator: PlatformLocator,
        blog: Blog
    ): NavKey? {
        return null
    }

    override fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        if (blog.platform.protocol.notBluesky) return null
        return null
    }

    override fun getContentScreen(
        content: FreadContent,
        isLatestTab: Boolean
    ): Tab {
        return BlueskyHomeTab(content.id, isLatestTab)
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): NavKey? {
        if (content !is BlueskyContent) return null
        return BskyFollowingFeedsPageNavKey(contentId = content.id, locator = null)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?,
    ): NavKey? {
        val did = userUriTransformer.parse(uri)?.did ?: return null
        return BskyUserDetailScreenNavKey(locator = locator, did = did)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): NavKey? {
        if (protocol.notBluesky) return null
        return BskyUserDetailScreenNavKey(locator = locator, did = did)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): NavKey? {
        if (protocol.notBluesky) return null
        return BskyUserDetailScreenNavKey(locator, webFinger.did ?: return null)
    }

    override fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol
    ): NavKey? {
        if (protocol.notBluesky) return null
        return HomeFeedsScreenNavKey.create(
            feeds = BlueskyFeeds.Hashtags(tag),
            locator = locator,
        )
    }

    override fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        if (protocol.notBluesky) return null
        return UserListScreenNavKey(
            locator = locator,
            type = UserListType.LIKE,
            postUri = blog.url,
        )
    }

    override fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        if (protocol.notBluesky) return null
        return UserListScreenNavKey(
            locator = locator,
            type = UserListType.REBLOG,
            postUri = blog.url,
        )
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        if (platform.protocol.notBluesky) return null
        return ExplorerTab(locator)
    }

    override fun getAddContentScreen(protocol: StatusProviderProtocol): NavKey? {
        if (protocol.notBluesky) return null
        return AddBlueskyContentScreenNavKey()
    }

    override fun getPublishScreen(account: LoggedAccount, text: String): NavKey? {
        if (account !is BlueskyLoggedAccount) return null
        return PublishPostScreenNavKey(
            locator = account.locator,
            defaultText = text,
        )
    }
}
