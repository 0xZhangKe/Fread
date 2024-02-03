package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.composable.PagerTab
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
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

    fun getReplyBlogScreen(blog: Blog): String? {
        return providerList.mapFirstOrNull {
            it.getReplyBlogScreen(blog)
        }
    }

    fun getAddContentScreenRoute(contentType: ContentType): String? {
        return providerList.mapFirstOrNull {
            it.getAddContentScreenRoute(contentType)
        }
    }

    fun getContentScreen(contentConfig: ContentConfig): PagerTab? {
        return providerList.mapFirstOrNull {
            it.getContentScreen(contentConfig)
        }
    }

    fun getNotificationScreen(account: LoggedAccount): PagerTab? {
        return providerList.mapFirstOrNull {
            it.getNotificationScreen(account)
        }
    }

    fun getUserDetailRoute(uri: FormalUri): String? {
        return providerList.mapFirstOrNull { it.getUserDetailRoute(uri) }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreenRoute(config: ContentConfig): String?

    fun getPostStatusScreen(
        platform: BlogPlatform,
        accountUri: FormalUri? = null,
    ): String?

    fun getReplyBlogScreen(blog: Blog): String?

    fun getAddContentScreenRoute(contentType: ContentType): String?

    fun getContentScreen(contentConfig: ContentConfig): PagerTab?

    fun getNotificationScreen(account: LoggedAccount): PagerTab?

    fun getUserDetailRoute(uri: FormalUri): String?
}
