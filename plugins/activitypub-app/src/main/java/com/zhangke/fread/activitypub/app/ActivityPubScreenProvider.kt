package com.zhangke.fread.activitypub.app

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigRoute
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.fread.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.fread.activitypub.app.internal.screen.notifications.ActivityPubNotificationsScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val loggedAccountProvider: LoggedAccountProvider,
) : IStatusScreenProvider {

    override fun getServerDetailScreenRoute(config: ContentConfig): String? {
        val activityPubContent = config as? ContentConfig.ActivityPubContent ?: return null
        return PlatformDetailRoute.buildRoute(activityPubContent.baseUrl)
    }

    override fun getPostStatusScreen(
        platform: BlogPlatform,
        accountUri: FormalUri?,
    ): String? {
        if (platform.protocol.notActivityPub) return null
        return if (accountUri == null) {
            PostStatusScreenRoute.ROUTE
        } else {
            PostStatusScreenRoute.buildRoute(accountUri)
        }
    }

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
}
