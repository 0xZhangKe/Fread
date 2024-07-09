package com.zhangke.fread.common.browser

import android.content.Context
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.utils.findImplementers

interface BrowserInterceptor {

    suspend fun intercept(context: Context, role: IdentityRole, url: String): Boolean
}
