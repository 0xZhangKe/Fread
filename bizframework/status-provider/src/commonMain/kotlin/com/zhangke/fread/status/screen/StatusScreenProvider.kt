package com.zhangke.fread.status.screen

import androidx.navigation3.runtime.NavKey
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
    ): NavKey? {
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

    fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): NavKey?

    fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): NavKey?

    fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): NavKey?

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab?

    fun getEditContentConfigScreenScreen(content: FreadContent): NavKey?

    fun getUserDetailScreen(locator: PlatformLocator, uri: FormalUri, userId: String?): NavKey?

    fun getUserDetailScreenWithoutAccount(uri: FormalUri): NavKey? {
        return null
    }

    fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): NavKey?

    fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): NavKey?

    fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol
    ): NavKey?

    fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): NavKey?

    fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): NavKey?

    fun getInstanceDetailScreen(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): NavKey? {
        return null
    }

    fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab?

    fun getAddContentScreen(protocol: StatusProviderProtocol): NavKey? {
        return null
    }

    fun getPublishScreen(account: LoggedAccount, text: String): NavKey? {
        return null
    }
}
