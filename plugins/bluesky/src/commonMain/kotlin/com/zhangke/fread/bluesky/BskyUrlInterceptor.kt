package com.zhangke.fread.bluesky

import androidx.navigation3.runtime.NavKey
import app.bsky.actor.GetProfileQueryParams
import app.bsky.feed.GetPostThreadQueryParams
import app.bsky.feed.GetPostThreadResponseThreadUnion
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
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreenNavKey
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreenNavKey
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.createBlueskyProtocol
import sh.christian.ozone.api.Handle
import sh.christian.ozone.api.RKey

class BskyUrlInterceptor(
    private val accountManager: BlueskyLoggedAccountManager,
    private val clientManager: BlueskyClientManager,
    private val statusAdapter: BlueskyStatusAdapter,
    private val platformRepo: BlueskyPlatformRepo,
    private val contentRepo: FreadContentRepo,
) : BrowserInterceptor {

    override suspend fun intercept(
        locator: PlatformLocator?,
        url: String,
        isFromExternal: Boolean,
    ): InterceptorResult {
        var uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (!HttpScheme.validate(uri.scheme.orEmpty().addProtocolSuffixIfNecessary())) {
            return InterceptorResult.CanNotIntercept
        }
        if (uri.host.isNullOrEmpty()) return InterceptorResult.CanNotIntercept
        val isProfileUrl = isProfileUrl(uri)
        val isPostUrl = isPostUrl(uri)
        if (!isProfileUrl && !isPostUrl) {
            if (isFromExternal) {
                if (platformRepo.appViewDomains.any { uri.host == it }) {
                    val content = contentRepo.getAllContent()
                        .firstNotNullOfOrNull { it as? BlueskyContent }
                    if (content != null) {
                        return InterceptorResult.SwitchHomeContent(content)
                    }
                }
            }
            return InterceptorResult.CanNotIntercept
        }
        uri = uri.copy(host = platformRepo.mapAppToBackendDomain(uri.host!!))
        val baseUrl = locator?.baseUrl ?: FormalBaseUrl.parse(uri.host!!)
        ?: return InterceptorResult.CanNotIntercept
        var account: BlueskyLoggedAccount? = null
        val fixedLocator = if (locator?.accountUri != null) {
            locator
        } else {
            val accounts = accountManager.getAllAccount()
                .filter { it.fromPlatform.baseUrl == baseUrl }
            if (accounts.isEmpty()) {
                locator ?: PlatformLocator(baseUrl)
            } else if (accounts.size == 1) {
                account = accounts.first()
                PlatformLocator(account.platform.baseUrl, account.uri)
            } else {
                return InterceptorResult.RequireSelectAccount(createBlueskyProtocol())
            }
        }
        parseProfile(fixedLocator, uri)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        parsePost(fixedLocator, uri, account)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        return InterceptorResult.CanNotIntercept
    }

    private fun isProfileUrl(uri: SimpleUri): Boolean {
        val path = uri.path
        if (path.isNullOrEmpty()) return false
        if (!path.startsWith("/profile/")) return false
        val handle = path.split('/').lastOrNull()
        if (handle.isNullOrEmpty()) return false
        if (!handle.contains('.')) return false
        return true
    }

    private suspend fun parseProfile(locator: PlatformLocator, uri: SimpleUri): NavKey? {
        if (!isProfileUrl(uri)) return null
        val handle = uri.path?.split('/')?.lastOrNull()
        if (handle.isNullOrEmpty()) return null
        val profile = clientManager.getClient(locator)
            .getProfileCatching(GetProfileQueryParams(Handle(handle)))
            .getOrNull()
        if (profile == null) return null
        return BskyUserDetailScreenNavKey(locator = locator, did = profile.did.did)
    }

    private fun isPostUrl(uri: SimpleUri): Boolean {
        val path = uri.path
        if (path.isNullOrEmpty()) return false
        val groupedPath = path.removePrefix("/").removeSuffix("/").split('/')
        if (groupedPath.size != 4) return false
        if (groupedPath[0] != "profile") return false
        if (groupedPath[2] != "post") return false
        return true
    }

    private suspend fun parsePost(
        locator: PlatformLocator,
        uri: SimpleUri,
        account: BlueskyLoggedAccount?
    ): NavKey? {
        if (!isPostUrl(uri)) return null
        val groupedPath = uri.path
            ?.removePrefix("/")
            ?.removeSuffix("/")
            ?.split('/')
            ?: return null
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
        return StatusContextScreenNavKey.create(statusUiState)
    }
}
