package com.zhangke.fread.activitypub.app

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreen
import com.zhangke.fread.activitypub.app.internal.screen.explorer.ExplorerContainerTab
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreen
import com.zhangke.fread.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListType
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListType
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreen
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val loggedAccountProvider: LoggedAccountProvider,
) : IStatusScreenProvider {

    override fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = locator.accountUri
        if (accountUri == null) {
            accountUri = loggedAccountProvider.getAccount(locator.baseUrl)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildReplyScreen(
            accountUri = accountUri,
            blog = blog,
        )
    }

    override fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = locator.accountUri
        if (accountUri == null) {
            accountUri = loggedAccountProvider.getAccount(locator.baseUrl)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildEditBlogRoute(
            accountUri = accountUri,
            blog = blog,
        )
    }

    override fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        return null
    }

    override fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab? {
        if (content !is ActivityPubContent) return null
        return ActivityPubContentScreen(content.id, isLatestTab)
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): Screen? {
        if (content !is ActivityPubContent) return null
        return EditContentConfigScreen(content.id)
    }

    override suspend fun getEditContentConfigScreenScreen(account: LoggedAccount): Screen? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?,
    ): Screen? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailScreen(locator = locator, userUri = uri, userId = userId)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserDetailScreen(locator = locator, webFinger = webFinger)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): Screen? {
        return null
    }

    override fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ): Screen? {
        if (protocol.notActivityPub) return null
        return HashtagTimelineScreen(
            locator = locator,
            hashtag = tag.removePrefix("#"),
        )
    }

    override fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserListScreen(
            locator = locator,
            type = UserListType.FAVOURITES,
            statusId = blog.id,
        )
    }

    override fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserListScreen(
            locator = locator,
            type = UserListType.REBLOGS,
            statusId = blog.id,
        )
    }

    override fun getBookmarkedScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return StatusListScreen(
            locator = locator,
            type = StatusListType.BOOKMARKS,
        )
    }

    override fun getFavouritedScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return StatusListScreen(
            locator = locator,
            type = StatusListType.FAVOURITES,
        )
    }

    override fun getFollowedHashtagScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return TagListScreen(locator)
    }

    override fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl
    ): String? {
        if (protocol.notActivityPub) return null
        return PlatformDetailRoute.buildRoute(baseUrl)
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): PagerTab? {
        if (platform.protocol.notActivityPub) return null
        return ExplorerContainerTab(locator = locator, platform = platform)
    }

    override fun getCreatedListScreen(
        locator: PlatformLocator,
        platform: BlogPlatform
    ): Screen? {
        if (platform.protocol.notActivityPub) return null
        return CreatedListsScreen(
            locator = locator,
        )
    }
}
