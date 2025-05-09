package com.zhangke.fread.common.browser

import com.zhangke.fread.status.model.IdentityRole

interface BrowserInterceptor {

    suspend fun intercept(role: IdentityRole, url: String): Boolean
}
