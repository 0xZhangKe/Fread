package com.zhangke.fread.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ActivityPubUrlInterceptor @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val clientManager: ActivityPubClientManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : BrowserInterceptor {

    override suspend fun intercept(locator: PlatformLocator?, url: String): InterceptorResult {
        val uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (HttpScheme.validate(uri.scheme.orEmpty())) return InterceptorResult.CanNotIntercept
        val (finalLocator, account) = if (locator == null) {
            val baseUrl = FormalBaseUrl.parse(url) ?: return InterceptorResult.CanNotIntercept
            val platform = platformRepo.getPlatform(baseUrl).getOrNull()
                ?: return InterceptorResult.CanNotIntercept
            val account = loggedAccountProvider.getAccount(platform.baseUrl)
            PlatformLocator(baseUrl = platform.baseUrl, accountUri = account?.uri) to account
        } else {
            val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
            locator to account
        }
        val status = parseStatus(finalLocator, uri, account)
        if (status != null) {
            return InterceptorResult.SuccessWithOpenNewScreen(StatusContextScreen.create(status))
        }
        val webFinger = parseActivityPubUser(finalLocator, uri)
        if (webFinger != null) {
            return InterceptorResult.SuccessWithOpenNewScreen(
                UserDetailScreen(locator = finalLocator, webFinger = webFinger)
            )
        }
        val platform = parsePlatform(uri)
        if (platform != null) {
            return InterceptorResult.SuccessWithOpenNewScreen(
                InstanceDetailScreen(
                    finalLocator,
                    platform.baseUrl
                )
            )
        }
        return InterceptorResult.CanNotIntercept
    }

    private suspend fun parseActivityPubUser(locator: PlatformLocator, uri: SimpleUri): WebFinger? {
        val path = uri.path?.removePrefix("/") ?: return null
        if (path.isEmpty()) return null
        if (uri.queries.isNotEmpty()) return null
        if (path.contains("?") || path.contains("/")) return null
        if (!path.startsWith("@")) return null
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val acct = "$path@${baseUrl.host}"
        val accountRepo = clientManager.getClient(locator).accountRepo
        val account = accountRepo.lookup(acct).getOrNull() ?: return null
        return accountEntityAdapter.toWebFinger(account)
    }

    private suspend fun parseStatus(
        locator: PlatformLocator,
        uri: SimpleUri,
        account: ActivityPubLoggedAccount?,
    ): StatusUiState? {
        if (uri.queries.isNotEmpty()) return null
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val path = uri.path.toString().removePrefix("/")
        val array = path.split("/")
        if (array.size != 2) return null
        val acct = array[0]
        val statusId = array[1]
        if (!acct.startsWith("@")) return null
        val client = clientManager.getClient(locator)
        val statusRepo = client.statusRepo
        val platform = platformRepo.getPlatform(baseUrl).getOrNull() ?: return null
        if (locator.baseUrl != baseUrl) {
            // other platform, by search
            val searchRepo = client.searchRepo
            val searchedStatusResult =
                searchRepo.queryStatus(uri.toString(), resolve = true).getOrNull() ?: return null
            if (searchedStatusResult.size != 1) return null
            return searchedStatusResult.first()
                .let {
                    activityPubStatusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        locator = locator,
                        loggedAccount = account,
                    )
                }
        } else {
            // same platform
            return statusRepo.getStatuses(statusId)
                .getOrNull()
                ?.let {
                    activityPubStatusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        locator = locator,
                        loggedAccount = account,
                    )
                }
        }
    }

    private suspend fun parsePlatform(uri: SimpleUri): BlogPlatform? {
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        if (uri.queries.isNotEmpty()) return null
        return platformRepo.getPlatform(baseUrl).getOrNull()
    }
}
