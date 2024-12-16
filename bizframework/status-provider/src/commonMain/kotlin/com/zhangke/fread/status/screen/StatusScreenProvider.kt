package com.zhangke.fread.status.screen

import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String? {
        return providerList.firstNotNullOfOrNull {
            it.getReplyBlogScreen(role, blog)
        }
    }

    suspend fun getEditBlogScreen(role: IdentityRole, blog: Blog): String? {
        return providerList.firstNotNullOfOrNull {
            it.getEditBlogScreen(role, blog)
        }
    }

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab? {
        return providerList.firstNotNullOfOrNull {
            it.getContentScreen(content, isLatestTab)
        }
    }

    fun getEditContentConfigScreenRoute(content: FreadContent): String? {
        return providerList.firstNotNullOfOrNull {
            it.getEditContentConfigScreenRoute(content)
        }
    }

    fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        return providerList.firstNotNullOfOrNull {
            it.getNotificationScreen(account)
        }
    }

    fun getUserDetailRoute(role: IdentityRole, uri: FormalUri): String? {
        return providerList.firstNotNullOfOrNull { it.getUserDetailRoute(role, uri) }
    }

    fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull {
            it.getUserDetailRoute(
                role,
                webFinger,
                protocol
            )
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
        blogId: String,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull {
            it.getBlogFavouritedScreen(
                role,
                blogId,
                protocol
            )
        }
    }

    fun getBlogBoostedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull { it.getBlogBoostedScreen(role, blogId, protocol) }
    }

    fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.firstNotNullOfOrNull { it.getBookmarkedScreen(role, protocol) }
    }

    fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String? {
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
}

interface IStatusScreenProvider {

    suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String?

    suspend fun getEditBlogScreen(role: IdentityRole, blog: Blog): String?

    fun getContentScreen(content: FreadContent, isLatestTab: Boolean): PagerTab?

    fun getEditContentConfigScreenRoute(content: FreadContent): String?

    fun getNotificationScreen(account: LoggedAccount): PagerTab?

    fun getUserDetailRoute(role: IdentityRole, uri: FormalUri): String?

    fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol
    ): String?

    fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol
    ): String?

    fun getBlogFavouritedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol,
    ): String?

    fun getBlogBoostedScreen(
        role: IdentityRole,
        blogId: String,
        protocol: StatusProviderProtocol,
    ): String?

    fun getBookmarkedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String?

    fun getFavouritedScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String?

    fun getFollowedHashtagScreen(
        role: IdentityRole,
        protocol: StatusProviderProtocol,
    ): String?

    fun getInstanceDetailScreen(
        protocol: StatusProviderProtocol,
        baseUrl: FormalBaseUrl,
    ): String?

}
