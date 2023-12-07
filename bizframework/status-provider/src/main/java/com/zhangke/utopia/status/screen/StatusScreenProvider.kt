package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getPlatformDetailScreen(platformUri: String): Any? {
        val uri = StatusProviderUri.from(platformUri) ?: return null
        return providerList.mapFirstOrNull {
            it.getServerDetailScreen(uri)
        }
    }

    fun getPostStatusScreen(platform: BlogPlatform): Any? {
        return providerList.mapFirstOrNull {
            it.getPostStatusScreen(platform)
        }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreen(platformUri: StatusProviderUri): Any?

    fun getPostStatusScreen(platform: BlogPlatform): Any?
}
