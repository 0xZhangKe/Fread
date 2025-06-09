package com.zhangke.fread.status.screen

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getReplyBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getReplyBlogScreen(role, blog)
        }
    }

    fun getEditBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getEditBlogScreen(role, blog)
        }
    }

    fun getQuoteBlogScreen(role: IdentityRole, blog: Blog): Screen? {
        return providerList.firstNotNullOfOrNull { it.getQuoteBlogScreen(role, blog) }
    }

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab? {
        return providerList.firstNotNullOfOrNull {
            it.getContentScreen(content, isLatestTab)
        }
    }

    fun getEditContentConfigScreenScreen(content: FreadContent): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getEditContentConfigScreenScreen(content)
        }
    }

    suspend fun getEditContentConfigScreenScreen(account: LoggedAccount): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getEditContentConfigScreenScreen(account)
        }
    }

    fun getUserDetailScreen(
        role: IdentityRole,
        uri: FormalUri,
        userId: String?,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getUserDetailScreen(role, uri, userId) }
    }

    fun getUserDetailScreen(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailScreen(role, webFinger, protocol)
        }
    }

    fun getUserDetailScreen(
        role: IdentityRole,
        did: String,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailScreen(role, did, protocol)
        }
    }

    fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull {
            it.getTagTimelineScreenRoute(
                role,
                tag,
                protocol
            )
        }
    }

    fun getBlogFavouritedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull {
            it.getBlogFavouritedScreen(role, blog, protocol)
        }
    }

    fun getBlogBoostedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getBlogBoostedScreen(role, blog, protocol) }
    }

    fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getBookmarkedScreen(role, protocol) }
    }

    fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): Screen? {
        return providerList.firstNotNullOfOrNull { it.getFavouritedScreen(role, protocol) }
    }

    fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull { it.getFollowedHashtagScreen(role, protocol) }
    }

    fun getInstanceDetailScreen(
        baseUrl: FormalBaseUrl,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull { it.getInstanceDetailScreen(protocol, baseUrl) }
    }

    fun getExplorerTab(role: IdentityRole, platform: BlogPlatform): PagerTab? {
        return providerList.firstNotNullOfOrNull { it.getExplorerTab(role, platform) }
    }

    fun getCreatedListScreen(role: IdentityRole, platform: BlogPlatform): Screen? {
        return providerList.firstNotNullOfOrNull { it.getCreatedListScreen(role, platform) }
    }
}

interface IStatusScreenProvider {

    fun getReplyBlogScreen(role: IdentityRole, blog: Blog): Screen?

    fun getEditBlogScreen(role: IdentityRole, blog: Blog): Screen?

    fun getQuoteBlogScreen(role: IdentityRole, blog: Blog): Screen?

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab?

    fun getEditContentConfigScreenScreen(content: FreadContent): Screen?

    suspend fun getEditContentConfigScreenScreen(account: LoggedAccount): Screen?

    fun getUserDetailScreen(role: IdentityRole, uri: FormalUri, userId: String?): Screen?

    fun getUserDetailScreen(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): Screen?

    fun getUserDetailScreen(
        role: IdentityRole,
        did: String,
        protocol: StatusProviderProtocol
    ): Screen?

    fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol
    ): String?

    fun getBlogFavouritedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getBlogBoostedScreen(
        role: IdentityRole,
        blog: Blog,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): Screen?

    fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String?

    fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): String?

    fun getExplorerTab(role: IdentityRole, platform: BlogPlatform): PagerTab?

    fun getCreatedListScreen(role: IdentityRole, platform: BlogPlatform): Screen?
}
