package com.zhangke.fread.bluesky.internal.client

import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.repo.BlueskyLoggedAccountRepo
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class BlueskyClientManager @Inject constructor(
    private val loggedAccountRepo: BlueskyLoggedAccountRepo,
    private val accountAdapter: BlueskyAccountAdapter,
) {

    private val cachedClient = mutableMapOf<IdentityRole, BlueskyClient>()

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
    }

    fun getClient(role: IdentityRole): BlueskyClient {
        cachedClient[role]?.let { return it }
        val loggedAccountProvider = suspend { getLoggedAccount(role) }
        return createClient(role, loggedAccountProvider).also { cachedClient[role] = it }
    }

    private fun createClient(
        role: IdentityRole,
        loggedAccountProvider: suspend () -> BlueskyLoggedAccount?,
    ): BlueskyClient {
        return BlueskyClient(
            baseUrl = role.baseUrl!!,
            engine = httpClientEngine,
            json = globalJson,
            loggedAccountProvider = loggedAccountProvider,
            newSessionUpdater = { updateNewSession(role, it) },
            onLoginRequest = {

            },
        )
    }

    private suspend fun updateNewSession(role: IdentityRole, session: RefreshSessionResponse) {
        val account = getLoggedAccount(role) ?: return
        val newAccount = accountAdapter.updateNewSession(account, session)
        loggedAccountRepo.updateAccount(account, newAccount)
    }

    private suspend fun getLoggedAccount(role: IdentityRole): BlueskyLoggedAccount? {
        if (role.accountUri != null) {
            loggedAccountRepo.queryByUri(role.accountUri.toString())?.let { return it }
        }
        if (role.baseUrl == null) return null
        return loggedAccountRepo.queryAll()
            .firstOrNull { it.platform.baseUrl.equalsDomain(role.baseUrl!!) }
    }
}
