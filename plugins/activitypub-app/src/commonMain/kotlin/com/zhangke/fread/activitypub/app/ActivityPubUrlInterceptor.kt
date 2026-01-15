package com.zhangke.fread.activitypub.app

import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.network.addProtocolSuffixIfNecessary
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.InterceptorResult
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ActivityPubUrlInterceptor @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val clientManager: ActivityPubClientManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val contentRepo: FreadContentRepo,
) : BrowserInterceptor {

    override suspend fun intercept(
        locator: PlatformLocator?,
        url: String,
        isFromExternal: Boolean,
    ): InterceptorResult {
        val uri = SimpleUri.parse(url) ?: return InterceptorResult.CanNotIntercept
        if (!HttpScheme.validate(uri.scheme.orEmpty().addProtocolSuffixIfNecessary())) {
            return InterceptorResult.CanNotIntercept
        }
        val isProfileUrl = isProfileUrl(uri)
        val isStatusUrl = isMastodonStatusUrl(uri)
        if (isFromExternal && !isProfileUrl && !isStatusUrl) {
            val platform = parsePlatform(uri)
            if (platform != null) {
                val content = contentRepo.getAllContent()
                    .mapNotNull { it as? ActivityPubContent }
                    .firstOrNull { it.baseUrl == platform.baseUrl }
                if (content != null) {
                    return InterceptorResult.SwitchHomeContent(content)
                }
            }
            return InterceptorResult.CanNotIntercept
        }
        var account: ActivityPubLoggedAccount? = null
        val fixedLocator = if (locator?.accountUri != null) {
            locator
        } else {
            val accountList = loggedAccountProvider.getAllAccounts()
            if (accountList.isEmpty()) {
                val baseUrl = locator?.baseUrl ?: FormalBaseUrl.parse(uri.host!!)
                ?: return InterceptorResult.CanNotIntercept
                locator ?: PlatformLocator(baseUrl)
            } else if (accountList.size == 1) {
                account = accountList.first()
                PlatformLocator(account.platform.baseUrl, account.uri)
            } else {
                return InterceptorResult.RequireSelectAccount(createActivityPubProtocol())
            }
        }
        parseStatus(fixedLocator, uri, account)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        parseMastodonProfile(fixedLocator, uri)?.let {
            return InterceptorResult.SuccessWithOpenNewScreen(it)
        }
        if (!isFromExternal) {
            val platform = parsePlatform(uri)
            if (platform != null) {
                return InterceptorResult.SuccessWithOpenNewScreen(
                    InstanceDetailScreen(fixedLocator, platform.baseUrl)
                )
            }
        }
        return InterceptorResult.CanNotIntercept
    }

    private fun isProfileUrl(uri: SimpleUri): Boolean {
        val path = uri.path?.removePrefix("/") ?: return false
        if (path.isEmpty()) return false
        if (uri.queries.isNotEmpty()) return false
        if (path.contains("?") || path.contains("/")) return false
        if (!path.startsWith("@")) return false
        return true
    }

    private suspend fun parseMastodonProfile(locator: PlatformLocator, uri: SimpleUri): NavKey? {
        if (!isProfileUrl(uri)) return null
        val path = uri.path?.removePrefix("/") ?: return null
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val acct = "$path@${baseUrl.host}"
        val accountRepo = clientManager.getClient(locator).accountRepo
        val account = accountRepo.lookup(acct).getOrNull() ?: return null
        val webFinger = accountEntityAdapter.toWebFinger(account)
        return UserDetailScreen(locator = locator, webFinger = webFinger)
    }

    private fun isMastodonStatusUrl(uri: SimpleUri): Boolean {
        return parseMastodonStatusParams(uri) != null
    }

    private fun parseMastodonStatusParams(uri: SimpleUri): String? {
        FormalBaseUrl.parse(uri.toString()) ?: return null
        if (uri.queries.isNotEmpty()) return null
        val path = uri.path?.removePrefix("/") ?: return null
        val array = path.split("/")
        if (array.size != 2) return null
        val acct = array[0]
        val statusId = array[1]
        if (!acct.startsWith("@")) return null
        if (statusId.isEmpty()) return null
        return statusId
    }

    private suspend fun parseStatus(
        locator: PlatformLocator,
        uri: SimpleUri,
        account: ActivityPubLoggedAccount?,
    ): Screen? {
        val statusId = parseMastodonStatusParams(uri) ?: return null
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val client = clientManager.getClient(locator)
        val statusRepo = client.statusRepo
        val platform = platformRepo.getPlatform(baseUrl).getOrNull() ?: return null
        val status = if (locator.baseUrl != baseUrl) {
            // other platform, by search
            val searchRepo = client.searchRepo
            val searchedStatusResult =
                searchRepo.queryStatus(uri.toString(), resolve = true).getOrNull() ?: return null
            if (searchedStatusResult.size != 1) return null
            searchedStatusResult.first().let {
                activityPubStatusAdapter.toStatusUiState(
                    entity = it,
                    platform = platform,
                    locator = locator,
                    loggedAccount = account,
                )
            }
        } else {
            // same platform
            statusRepo.getStatuses(statusId).getOrNull()?.let {
                activityPubStatusAdapter.toStatusUiState(
                    entity = it,
                    platform = platform,
                    locator = locator,
                    loggedAccount = account,
                )
            }
        }
        return status?.let { StatusContextScreen.create(it) }
    }

    private suspend fun parsePlatform(uri: SimpleUri): BlogPlatform? {
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        if (uri.queries.isNotEmpty()) return null
        return platformRepo.getPlatform(baseUrl).getOrNull()
    }
}
