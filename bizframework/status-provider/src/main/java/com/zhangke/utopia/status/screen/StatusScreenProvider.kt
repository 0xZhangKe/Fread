package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getPlatformDetailScreenRoute(config: ContentConfig): String? {
        return providerList.mapFirstOrNull {
            it.getServerDetailScreenRoute(config)
        }
    }

    fun getPostStatusScreen(
        platform: BlogPlatform,
        accountUri: FormalUri? = null,
    ): String? {
        return providerList.mapFirstOrNull {
            it.getPostStatusScreen(platform, accountUri)
        }
    }

    suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String? {
        return providerList.mapFirstOrNull {
            it.getReplyBlogScreen(role, blog)
        }
    }

    fun getContentScreen(contentConfig: ContentConfig, isLatestTab: Boolean): PagerTab? {
        return providerList.mapFirstOrNull {
            it.getContentScreen(contentConfig, isLatestTab)
        }
    }

    fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String? {
        return providerList.mapFirstOrNull {
            it.getEditContentConfigScreenRoute(contentConfig)
        }
    }

    fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        return providerList.mapFirstOrNull {
            it.getNotificationScreen(account)
        }
    }

    fun getUserDetailRoute(role: IdentityRole, uri: FormalUri): String? {
        return providerList.mapFirstOrNull { it.getUserDetailRoute(role, uri) }
    }

    fun getUserDetailRoute(
        role: IdentityRole,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.mapFirstOrNull { it.getUserDetailRoute(role, webFinger, protocol) }
    }

    fun getTagTimelineScreenRoute(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol,
    ): String? {
        return providerList.mapFirstOrNull { it.getTagTimelineScreenRoute(role, tag, protocol) }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreenRoute(config: ContentConfig): String?

    fun getPostStatusScreen(
        platform: BlogPlatform,
        accountUri: FormalUri? = null,
    ): String?

    suspend fun getReplyBlogScreen(role: IdentityRole, blog: Blog): String?

    fun getContentScreen(contentConfig: ContentConfig, isLatestTab: Boolean): PagerTab?

    fun getEditContentConfigScreenRoute(contentConfig: ContentConfig): String?

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
}
