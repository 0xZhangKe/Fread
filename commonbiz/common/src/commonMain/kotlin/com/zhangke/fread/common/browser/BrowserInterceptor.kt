package com.zhangke.fread.common.browser

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol

interface BrowserInterceptor {

    suspend fun intercept(
        locator: PlatformLocator?,
        url: String,
        isFromExternal: Boolean,
    ): InterceptorResult
}

sealed interface InterceptorResult {

    data object CanNotIntercept : InterceptorResult

    data class SuccessWithOpenNewScreen(val screen: Screen) : InterceptorResult

    data class SwitchHomeContent(val content: FreadContent) : InterceptorResult

    data class RequireSelectAccount(val protocol: StatusProviderProtocol) : InterceptorResult
}
