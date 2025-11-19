package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Handle

class BskyUrlInterceptor @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
) : BrowserInterceptor {

    override suspend fun intercept(locator: PlatformLocator?, url: String): InterceptorResult {
        val uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (HttpScheme.validate(uri.scheme.orEmpty())) return InterceptorResult.CanNotIntercept
        val finalLocator = if (locator == null) {
            val baseUrl = FormalBaseUrl.parse(url) ?: return InterceptorResult.CanNotIntercept
            val account = accountManager.getAccount(PlatformLocator(baseUrl = baseUrl))
            PlatformLocator(baseUrl = baseUrl, accountUri = account?.uri)
        } else {
            locator
        }
        parseProfile(finalLocator, uri)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        return InterceptorResult.CanNotIntercept
    }

    private suspend fun parseProfile(locator: PlatformLocator, uri: SimpleUri): Screen? {
        val path = uri.path
        if (path.isNullOrEmpty()) return null
        if (!path.startsWith("/profile/")) return null
        val handle = path.split('/').lastOrNull()
        if (handle.isNullOrEmpty()) return null
        if (!handle.contains('.')) return null
        val profile = clientManager.getClient(locator)
            .getProfileCatching(GetProfileQueryParams(Handle(handle)))
            .getOrNull()
        if (profile == null) return null
        return BskyUserDetailScreen(locator = locator, did = profile.did.did)
    }

//    private suspend fun parsePost(
//        locator: PlatformLocator,
//        uri: SimpleUri,
//        account: BlueskyLoggedAccount?
//    ): Screen? {
//        val path = uri.path
//        if (path.isNullOrEmpty()) return null
//        val groupedPath = path.removePrefix("/").removeSuffix("/").split('/')
//        if (groupedPath.size != 4) return null
//        if (groupedPath[0] != "profile") return null
//        if (groupedPath[2] != "post") return null
//        val post = clientManager.getClient(locator)
//            .searchPostsByUri(uri.toString())
//            .getOrNull()?.posts?.firstOrNull()
//        if (post == null) return null
//        val statusUiState = statusAdapter.convertToUiState(
//            postView = post,
//            locator = locator,
//            platform = platformRepo.getPlatform(locator.baseUrl),
//            loggedAccount = account,
//            pinned = false,
//        )
//        return StatusContextScreen.create(statusUiState)
//    }
}
