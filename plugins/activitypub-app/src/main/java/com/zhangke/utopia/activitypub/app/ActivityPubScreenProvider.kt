package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.utopia.activitypub.app.internal.screen.content.edit.EditContentConfigRoute
import com.zhangke.utopia.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.utopia.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.ActivityPubNotificationsScreen
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.FormalUri
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
        if (platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID) return null
        return if (accountUri == null) {
            PostStatusScreenRoute.ROUTE
        } else {
            PostStatusScreenRoute.buildRoute(accountUri)
        }
    }

    override suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String? {
        if (blog.platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID) return null
        var accountUri = role.accountUri
        if (accountUri == null && role.baseUrl != null) {
            accountUri = loggedAccountProvider.getAccount(role.baseUrl!!)?.uri
        }
        accountUri ?: return null
        return PostStatusScreenRoute.buildRoute(accountUri, blog.id, blog.author.name)
    }

    override fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        if (contentConfig !is ContentConfig.ActivityPubContent) return null
        return ActivityPubContentScreen(contentConfig.id)
    }

    override fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String? {
        if (contentConfig !is ContentConfig.ActivityPubContent) return null
        return EditContentConfigRoute.buildRoute(contentConfig.id)
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        if (account.platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID) return null
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
        if (protocol.id != ACTIVITY_PUB_PROTOCOL_ID) return null
        return UserDetailRoute.buildRoute(role, webFinger)
    }

    override fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol,
    ): String? {
        if (!protocol.isActivityPub) return null
        return HashtagTimelineRoute.buildRoute(role, tag)
    }
}
