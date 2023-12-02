package com.zhangke.utopia.status.screen

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.utopia.status.platform.BlogPlatform

class StatusScreenProvider(
    private val providerList: List<IStatusScreenProvider>
) {

    fun getServerDetailScreen(serverUri: String): Any? {
        return providerList.mapFirstOrNull {
            it.getServerDetailScreen(serverUri)
        }
    }

    fun getPostStatusScreen(platform: BlogPlatform): Any? {
        return providerList.mapFirstOrNull {
            it.getPostStatusScreen(platform)
        }
    }
}

interface IStatusScreenProvider {

    fun getServerDetailScreen(serverUri: String): Any?

    fun getPostStatusScreen(platform: BlogPlatform): Any?
}
