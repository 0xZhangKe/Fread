package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.composable.PagerTab
import com.zhangke.utopia.activitypub.app.internal.screen.addinstance.AddInstanceScreenRoute
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.utopia.activitypub.app.internal.screen.instance.PlatformDetailRoute
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.ActivityPubNotificationsScreen
import com.zhangke.utopia.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.screen.IStatusScreenProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubScreenProvider @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
) : IStatusScreenProvider {

    override fun getServerDetailScreenRoute(config: ContentConfig): String? {
        val activityPubContent = config as? ContentConfig.ActivityPubContent ?: return null
        return PlatformDetailRoute.buildRoute(activityPubContent.baseUrl)
    }

    override fun getPostStatusScreen(
        platform: BlogPlatform,
        accountUri: FormalUri?,
    ): String? {
        if (platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return if (accountUri == null) {
            PostStatusScreenRoute.ROUTE
        } else {
            PostStatusScreenRoute.buildRoute(accountUri)
        }
    }

    override fun getReplyBlogScreen(blog: Blog): String? {
        if (blog.platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        return PostStatusScreenRoute.buildRoute(blog.id, blog.author.name)
    }

    override fun getAddContentScreenRoute(contentType: ContentType): String? {
        if (contentType != ContentType.ACTIVITY_PUB) return null
        return AddInstanceScreenRoute.ROOT
    }

    override fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        if (contentConfig !is ContentConfig.ActivityPubContent) return null
        return ActivityPubContentScreen(contentConfig.id)
    }

    override fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        if (account.platform.protocol != ACTIVITY_PUB_PROTOCOL) return null
        val userInsights = userUriTransformer.parse(account.uri) ?: return null
        return ActivityPubNotificationsScreen(userInsights)
    }

    override fun getUserDetailRoute(uri: FormalUri): String? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailRoute.buildRoute(uri)
    }
}
