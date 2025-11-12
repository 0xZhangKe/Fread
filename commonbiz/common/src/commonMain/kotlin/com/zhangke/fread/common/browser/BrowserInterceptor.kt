package com.zhangke.fread.common.browser

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.status.model.PlatformLocator

interface BrowserInterceptor {

    suspend fun intercept(locator: PlatformLocator?, url: String): InterceptorResult
}

sealed interface InterceptorResult {

    data object CanNotIntercept : InterceptorResult

    data class SuccessWithOpenNewScreen(val screen: Screen) : InterceptorResult
}
