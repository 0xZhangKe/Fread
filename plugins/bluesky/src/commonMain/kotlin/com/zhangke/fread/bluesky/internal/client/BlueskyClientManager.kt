package com.zhangke.fread.bluesky.internal.client

import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.throwInDebug
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.repo.BlueskyLoggedAccountRepo
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class BlueskyClientManager @Inject constructor(
    private val loggedAccountRepo: BlueskyLoggedAccountRepo,
    private val accountAdapter: BlueskyAccountAdapter,
    private val blueskyPlatformRepo: BlueskyPlatformRepo,
) {

    private val cachedClient = mutableMapOf<PlatformLocator, BlueskyClient>()
    private val cachedAccount = mutableMapOf<PlatformLocator, BlueskyLoggedAccount>()

    private val httpClientEngine by lazy {
        createHttpClientEngine()
    }

    init {
        ApplicationScope.launch {
            loggedAccountRepo.queryAllFlow().collect {
                clearCache()
            }
        }
    }

    fun clearCache() {
        cachedClient.clear()
        cachedAccount.clear()
    }

    fun getClient(locator: PlatformLocator): BlueskyClient {
        cachedClient[locator]?.let { return it }
        val loggedAccountProvider = suspend { getLoggedAccount(locator) }
        return createClient(locator, loggedAccountProvider).also { cachedClient[locator] = it }
    }

    fun getClientNoAccount(baseUrl: FormalBaseUrl): BlueskyClient {
        return BlueskyClient(
            baseUrl = baseUrl,
            engine = httpClientEngine,
            json = globalJson,
            loggedAccountProvider = { null },
            newSessionUpdater = { },
            onLoginRequest = {},
        )
    }

    private fun createClient(
        locator: PlatformLocator,
        loggedAccountProvider: suspend () -> BlueskyLoggedAccount?,
    ): BlueskyClient {
        return BlueskyClient(
            baseUrl = locator.baseUrl,
            engine = httpClientEngine,
            json = globalJson,
            loggedAccountProvider = loggedAccountProvider,
            newSessionUpdater = { updateNewSession(locator, it) },
            onLoginRequest = {
//                GlobalScreenNavigation.navigate(AddBlueskyContentScreen(role.baseUrl!!, true))
            },
        )
    }

    suspend fun updateNewSession(locator: PlatformLocator, session: RefreshSessionResponse) {
        cachedAccount.clear()
        val account = getLoggedAccount(locator) ?: return
        val newAccount = accountAdapter.updateNewSession(account, session)
        loggedAccountRepo.updateAccount(account, newAccount)
    }

    private suspend fun getLoggedAccount(locator: PlatformLocator): BlueskyLoggedAccount? {
        cachedAccount[locator]?.let { return it }
        if (locator.accountUri != null) {
            return loggedAccountRepo.queryByUri(locator.accountUri.toString())
                ?.also { cachedAccount[locator] = it }
        }
        val thisPlatformAccounts = loggedAccountRepo.queryAll()
            .filter { it.platform.baseUrl.equalsDomain(locator.baseUrl) }
        if (thisPlatformAccounts.size > 1) {
            throwInDebug("Multiple accounts found for base URL: ${locator.baseUrl}")
            return null
        }
        return thisPlatformAccounts.firstOrNull()
    }
}
