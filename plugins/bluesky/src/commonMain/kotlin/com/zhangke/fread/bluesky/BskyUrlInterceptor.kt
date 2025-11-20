package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
import cafe.adriel.voyager.core.screen.Screen
import com.atproto.repo.GetRecordQueryParams
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.network.addProtocolSuffixIfNecessary
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Handle
import sh.christian.ozone.api.RKey

class BskyUrlInterceptor @Inject constructor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
    private val statusAdapter: BlueskyStatusAdapter,
    private val platformRepo: BlueskyPlatformRepo,
) : BrowserInterceptor {

    override suspend fun intercept(locator: PlatformLocator?, url: String): InterceptorResult {
        var uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (!HttpScheme.validate(uri.scheme.orEmpty().addProtocolSuffixIfNecessary())) {
            return InterceptorResult.CanNotIntercept
        }
        uri = uri.copy(host = platformRepo.mapAppToBackendDomain(uri.host!!))
        val (finalLocator, account) = if (locator == null) {
            val baseUrl =
                FormalBaseUrl.parse(uri.toString()) ?: return InterceptorResult.CanNotIntercept
            val account = accountManager.getAccount(PlatformLocator(baseUrl = baseUrl))
            PlatformLocator(baseUrl = baseUrl, accountUri = account?.uri) to account
        } else {
            val account = accountManager.getAccount(locator)
            locator to account
        }
        parseProfile(finalLocator, uri)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        parsePost(finalLocator, uri, account)?.let {
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

    private suspend fun parsePost(
        locator: PlatformLocator,
        uri: SimpleUri,
        account: BlueskyLoggedAccount?
    ): Screen? {
        val path = uri.path
        if (path.isNullOrEmpty()) return null
        val groupedPath = path.removePrefix("/").removeSuffix("/").split('/')
        if (groupedPath.size != 4) return null
        if (groupedPath[0] != "profile") return null
        if (groupedPath[2] != "post") return null
        val client = clientManager.getClient(locator)
        val statusUiState = client.getRecordCatching(
            GetRecordQueryParams(
                repo = Handle(groupedPath[1]),
                collection = BskyCollections.feedPost,
                rkey = RKey(groupedPath[3]),
            )
        ).mapCatching { value ->
            client.getPostThreadCatching(GetPostThreadQueryParams(uri = value.uri))
                .map { (it.thread as? GetPostThreadResponseThreadUnion.ThreadViewPost)?.value?.post }
                .getOrNull()
        }.mapCatching { postView ->
            if (postView != null) {
                statusAdapter.convertToUiState(
                    postView = postView,
                    locator = locator,
                    platform = platformRepo.getPlatform(locator.baseUrl),
                    loggedAccount = account,
                    pinned = false,
                )
            } else {
                null
            }
        }.getOrNull() ?: return null
        return StatusContextScreen.create(statusUiState)
    }
}
