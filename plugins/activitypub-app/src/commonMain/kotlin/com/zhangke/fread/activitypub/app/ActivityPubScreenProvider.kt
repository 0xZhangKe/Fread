package com.zhangke.fread.activitypub.app

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.add.select.SelectPlatformScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentTab
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.explorer.ExplorerContainerTab
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreenKey
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

class ActivityPubScreenProvider(
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
        builder: (FormalUri, Blog) -> NavKey,
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
        return ActivityPubContentTab(content.id, isLatestTab)
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): NavKey? {
        if (content !is ActivityPubContent) return null
        return EditContentConfigScreenKey(content.id)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?,
    ): NavKey? {
        userUriTransformer.parse(uri) ?: return null
        return UserDetailScreenKey(locator = locator, userUri = uri, userId = userId)
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        if (protocol.notActivityPub) return null
        return UserDetailScreenKey(locator = locator, webFinger = webFinger)
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
        return HashtagTimelineScreenKey(
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
        return UserListScreenKey(
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
        return UserListScreenKey(
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
        return InstanceDetailScreenKey(locator = locator, baseUrl = baseUrl)
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        if (platform.protocol.notActivityPub) return null
        return ExplorerContainerTab(locator = locator, platform = platform)
    }

    override fun getAddContentScreen(protocol: StatusProviderProtocol): NavKey? {
        if (protocol.notActivityPub) return null
        return SelectPlatformScreenKey
    }

    override fun getPublishScreen(account: LoggedAccount, text: String): NavKey? {
        if (account !is ActivityPubLoggedAccount) return null
        return PostStatusScreenKey(accountUri = account.uri, defaultContent = text)
    }
}
