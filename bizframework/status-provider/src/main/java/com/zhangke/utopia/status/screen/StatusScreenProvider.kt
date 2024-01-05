package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.model.StatusProviderType
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.FormalUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getPlatformDetailScreen(platformUri: String): Any? {
        val uri = FormalUri.from(platformUri) ?: return null
        return providerList.mapFirstOrNull {
            it.getServerDetailScreen(uri)
        }
    }

    fun getPostStatusScreen(
        platform: BlogPlatform,
    ): Any? {
        return providerList.mapFirstOrNull {
            it.getPostStatusScreen(platform)
        }
    }

    fun getReplyBlogScreen(blog: Blog): Any? {
        return providerList.mapFirstOrNull {
            it.getReplyBlogScreen(blog)
        }
    }

    fun getAddContentScreen(statusProviderType: StatusProviderType): Any?{
        return providerList.mapFirstOrNull {
            it.getAddContentScreen(statusProviderType)
        }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreen(platformUri: FormalUri): Any?

    fun getPostStatusScreen(platform: BlogPlatform): Any?

    fun getReplyBlogScreen(blog: Blog): Any?

    fun getAddContentScreen(statusProviderType: StatusProviderType): Any?
}
