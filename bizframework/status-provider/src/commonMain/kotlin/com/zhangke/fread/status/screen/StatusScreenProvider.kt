package com.zhangke.fread.status.screen

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getReplyBlogScreen(locator, blog)
        }
    }

    fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getEditBlogScreen(locator, blog)
        }
    }

    fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull { it.getQuoteBlogScreen(locator, blog) }
    }

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab {
        return providerList.firstNotNullOf {
            it.getContentScreen(content, isLatestTab)
        }
    }

    fun getEditContentConfigScreenScreen(content: FreadContent): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getEditContentConfigScreenScreen(content)
        }
    }

    fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getUserDetailScreen(locator, uri, userId) }
    }

    fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailScreen(locator, webFinger, protocol)
        }
    }

    fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailScreen(locator, did, protocol)
        }
    }

    fun getUserDetailScreenWithoutAccount(uri: FormalUri): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailScreenWithoutAccount(uri)
        }
    }

    fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getTagTimelineScreen(
                locator,
                tag,
                protocol
            )
        }
    }

    fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getBlogFavouritedScreen(locator, blog, protocol)
        }
    }

    fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getBlogBoostedScreen(
                locator,
                blog,
                protocol
            )
        }
    }

    fun getInstanceDetailScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getInstanceDetailScreen(locator, protocol, baseUrl)
        }
    }

    fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        return providerList.firstNotNullOfOrNull { it.getExplorerTab(locator, platform) }
    }

    fun getAddContentScreen(protocol: StatusProviderProtocol): Screen {
        return providerList.firstNotNullOf {
            it.getAddContentScreen(protocol)
        }
    }

    fun getPublishScreen(
        account: LoggedAccount,
        text: String,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getPublishScreen(account, text) }
    }
}

interface IStatusScreenProvider {

    fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): Screen?

    fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): Screen?

    fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): Screen?

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab?

    fun getEditContentConfigScreenScreen(content: FreadContent): Screen?

    fun getUserDetailScreen(locator: PlatformLocator, uri: FormalUri, userId: String?): Screen?

    fun getUserDetailScreenWithoutAccount(uri: FormalUri): Screen? {
        return null
    }

    fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): Screen?

    fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): Screen?

    fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol
    ): Screen?

    fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getInstanceDetailScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): Screen? {
        return null
    }

    fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab?

    fun getAddContentScreen(protocol: StatusProviderProtocol): Screen? {
        return null
    }

    fun getPublishScreen(account: LoggedAccount, text: String): Screen? {
        return null
    }
}
