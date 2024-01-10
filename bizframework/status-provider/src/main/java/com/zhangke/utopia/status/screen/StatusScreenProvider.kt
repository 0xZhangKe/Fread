package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getPlatformDetailScreenRoute(platformUri: String): String? {
        val uri = FormalUri.from(platformUri) ?: return null
        return providerList.mapFirstOrNull {
            it.getServerDetailScreenRoute(uri)
        }
    }

    fun getPostStatusScreen(
        platform: BlogPlatform,
    ): String? {
        return providerList.mapFirstOrNull {
            it.getPostStatusScreen(platform)
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

    fun getContentScreen(contentConfig: ContentConfig): Any? {
        return providerList.mapFirstOrNull {
            it.getContentScreen(contentConfig)
        }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreenRoute(platformUri: FormalUri): String?

    fun getPostStatusScreen(platform: BlogPlatform): String?

    fun getReplyBlogScreen(blog: Blog): String?

    fun getAddContentScreenRoute(contentType: ContentType): String?

    fun getContentScreen(contentConfig: ContentConfig): Any?
}
