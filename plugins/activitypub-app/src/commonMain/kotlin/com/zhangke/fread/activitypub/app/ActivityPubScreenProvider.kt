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
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.fread.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListType
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListType
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreenRoute
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
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

    override fun getReplyBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = role.accountUri
        if (accountUri == null && role.baseUrl != null) {
            accountUri = loggedAccountProvider.getAccount(role.baseUrl!!)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildReplyScreen(
            accountUri = accountUri,
            blog = blog,
        )
    }

    override fun getEditBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = role.accountUri
        if (accountUri == null && role.baseUrl != null) {
            accountUri = loggedAccountProvider.getAccount(role.baseUrl!!)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildEditBlogRoute(
            accountUri = accountUri,
            blog = blog,
        )
    }

    override fun getQuoteBlogScreen(role: IdentityRole, blog: Blog): Screen? {
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

    override fun getUserDetailScreen(role: IdentityRole, uri: FormalUri): Screen? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailScreen(role = role, userUri = uri)
    }

    override fun getUserDetailScreen(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserDetailScreen(role = role, webFinger = webFinger)
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
        if (protocol.notActivityPub) return null
        return HashtagTimelineRoute.buildRoute(role, tag)
    }

    override fun getBlogFavouritedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserListScreen(
            role = role,
            type = UserListType.FAVOURITES,
            statusId = blog.id,
        )
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return UserListScreen(
            role = role,
            type = UserListType.REBLOGS,
            statusId = blog.id,
        )
    }

    override fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return StatusListScreen(
            role = role,
            type = StatusListType.BOOKMARKS,
        )
    }

    override fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): Screen? {
        if (protocol.notActivityPub) return null
        return StatusListScreen(
            role = role,
            type = StatusListType.FAVOURITES,
        )
    }

    override fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return TagListScreenRoute.buildRoute(role)
    }

    override fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl
    ): String? {
        if (protocol.notActivityPub) return null
        return PlatformDetailRoute.buildRoute(baseUrl)
    }

    override fun getExplorerTab(role: IdentityRole, platform: BlogPlatform): PagerTab? {
        if (platform.protocol.notActivityPub) return null
        return ExplorerContainerTab(role = role, platform = platform)
    }

    override fun getCreatedListScreen(
        role: IdentityRole,
        platform: BlogPlatform
    ): Screen? {
        if (platform.protocol.notActivityPub) return null
        return CreatedListsScreen(
            role = role,
        )
    }
}
