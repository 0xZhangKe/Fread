package com.zhangke.fread.common.browser

import com.zhangke.fread.status.model.PlatformLocator

interface BrowserInterceptor {

    suspend fun intercept(locator: PlatformLocator, url: String): Boolean
}
