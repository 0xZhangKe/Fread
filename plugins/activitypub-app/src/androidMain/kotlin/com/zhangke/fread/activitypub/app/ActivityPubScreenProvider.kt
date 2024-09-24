package com.zhangke.fread.activitypub.app

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigRoute
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.fread.activitypub.app.internal.screen.notifications.ActivityPubNotificationsScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListType
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreenRoute
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val loggedAccountProvider: LoggedAccountProvider,
) : IStatusScreenProvider {

    override suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = role.accountUri
        if (accountUri == null && role.baseUrl != null) {
            accountUri = loggedAccountProvider.getAccount(role.baseUrl!!)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildRoute(
            accountUri = accountUri,
            replyToBlogWebFinger = blog.author.webFinger,
            replyToBlogId = blog.id,
            replyAuthorName = blog.author.name,
            replyVisibility = blog.visibility,
        )
    }

    override suspend fun getEditBlogScreen(role: IdentityRole, blog: Blog): String? {
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

    override fun getContentScreen(contentConfig: ContentConfig, isLatestTab: Boolean): PagerTab? {
        if (contentConfig !is ContentConfig.ActivityPubContent) return null
        return ActivityPubContentScreen(contentConfig.id, isLatestTab)
    }

    override fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String? {
        if (contentConfig !is ContentConfig.ActivityPubContent) return null
        return EditContentConfigRoute.buildRoute(contentConfig.id)
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        if (account.platform.protocol.notActivityPub) return null
        val userInsights = userUriTransformer.parse(account.uri) ?: return null
        return ActivityPubNotificationsScreen(userInsights)
    }

    override fun getUserDetailRoute(role: IdentityRole, uri: FormalUri): String? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailRoute.buildRoute(role, uri)
    }

    override fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): String? {
        if (protocol.notActivityPub) return null
        return UserDetailRoute.buildRoute(role, webFinger)
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
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return UserListRoute.buildBlogFavouritedRoute(
            role = role,
            blogId = blogId,
        )
    }

    override fun getBlogBoostedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return UserListRoute.buildBlogBoostedRoute(
            role = role,
            blogId = blogId,
        )
    }

    override fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return StatusListScreenRoute.buildRoute(role, StatusListType.BOOKMARKS)
    }

    override fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return StatusListScreenRoute.buildRoute(role, StatusListType.FAVOURITES)
    }

    override fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol
    ): String? {
        if (protocol.notActivityPub) return null
        return TagListScreenRoute.buildRoute(role)
    }
}
