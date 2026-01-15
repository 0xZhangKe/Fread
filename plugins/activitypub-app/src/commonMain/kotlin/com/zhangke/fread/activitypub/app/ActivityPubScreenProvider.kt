package com.zhangke.fread.activitypub.app

import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.add.select.SelectPlatformScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreen
import com.zhangke.fread.activitypub.app.internal.screen.explorer.ExplorerContainerTab
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreen
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListType
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

    override fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return openPublishPostScreen(
            locator = locator,
            blog = blog,
        ) { accountUri, blog ->
            PostStatusScreenRoute.buildReplyScreen(
                accountUri = accountUri,
                blog = blog,
            )
        }
    }

    override fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return openPublishPostScreen(
            locator = locator,
            blog = blog,
        ) { accountUri, blog ->
            PostStatusScreenRoute.buildEditBlogRoute(
                accountUri = accountUri,
                blog = blog,
            )
        }
    }

    override fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return openPublishPostScreen(
            locator = locator,
            blog = blog,
        ) { accountUri, blog ->
            PostStatusScreenRoute.buildQuoteBlogScreen(
                accountUri = accountUri,
                quoteBlog = blog,
            )
        }
    }

    private fun openPublishPostScreen(
        locator: PlatformLocator,
        blog: Blog,
        builder: (FormalUri, Blog) -> Screen,
    ): NavKey? {
        if (blog.platform.protocol.notActivityPub) return null
        var accountUri = locator.accountUri
        if (accountUri == null) {
            accountUri = loggedAccountProvider.getAccount(locator.baseUrl)?.uri
        }
        accountUri ?: return null
        return builder(accountUri, blog)
    }

    override fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab? {
        if (content !is ActivityPubContent) return null
        return ActivityPubContentScreen(content.id, isLatestTab)
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): NavKey? {
        if (content !is ActivityPubContent) return null
        return EditContentConfigScreen(content.id)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?,
    ): NavKey? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailScreen(locator = locator, userUri = uri, userId = userId)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        if (protocol.notActivityPub) return null
        return UserDetailScreen(locator = locator, webFinger = webFinger)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ): NavKey? {
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
    ): NavKey? {
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
    ): NavKey? {
        if (protocol.notActivityPub) return null
        return UserListScreen(
            locator = locator,
            type = UserListType.REBLOGS,
            statusId = blog.id,
        )
    }

    override fun getInstanceDetailScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): NavKey? {
        if (protocol.notActivityPub) return null
        return InstanceDetailScreen(locator = locator, baseUrl = baseUrl)
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        if (platform.protocol.notActivityPub) return null
        return ExplorerContainerTab(locator = locator, platform = platform)
    }

    override fun getAddContentScreen(protocol: StatusProviderProtocol): NavKey? {
        if (protocol.notActivityPub) return null
        return SelectPlatformScreen()
    }

    override fun getPublishScreen(account: LoggedAccount, text: String): NavKey? {
        if (account !is ActivityPubLoggedAccount) return null
        return PostStatusScreen(accountUri = account.uri, defaultContent = text)
    }
}
