package com.zhangke.fread.bluesky

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class BskyUrlInterceptor @Inject constructor() : BrowserInterceptor {

    override suspend fun intercept(locator: PlatformLocator?, url: String): InterceptorResult {
        val uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (HttpScheme.validate(uri.scheme.orEmpty())) return InterceptorResult.CanNotIntercept
        parseProfile(locator, uri)
    }

    private suspend fun parseProfile(locator: PlatformLocator, uri: SimpleUri): Screen?{
        //https://bsky.app/profile/zhangke.space
        val path = uri.path
        if (path.isNullOrEmpty()) return null
        if (!path.contains("profile")
    }
}
