package com.zhangke.fread.bluesky.internal.account

import app.bsky.actor.GetProfileQueryParams
import app.bsky.actor.ProfileViewDetailed
import com.atproto.server.CreateSessionRequest
import com.atproto.server.CreateSessionResponse
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyLoggedAccountRepo
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.account.AccountRefreshResult
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class BlueskyLoggedAccountManager @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val accountAdapter: BlueskyAccountAdapter,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountRepo: BlueskyLoggedAccountRepo,
) {

    suspend fun login(
        baseUrl: FormalBaseUrl,
        identifier: String,
        password: String,
        factorToken: String? = null,
    ): Result<BlueskyLoggedAccount> {
        val noAccountClient = clientManager.getClientNoAccount(baseUrl)
        val sessionResult =
            noAccountClient.createSessionCatching(
                CreateSessionRequest(
                    identifier = identifier,
                    password = password,
                    authFactorToken = factorToken,
                )
            )
        if (sessionResult.isFailure) {
            return Result.failure(sessionResult.exceptionOrThrow())
        }
        val session = sessionResult.getOrThrow()
        val platform = platformRepo.getPlatform(baseUrl)
        val account = saveAccountToLocal(session, null, platform)
        val locator = PlatformLocator(baseUrl = baseUrl, accountUri = account.uri)
        val profileResult = clientManager.getClient(locator).getProfile(session.did.did)
        if (profileResult.isFailure) {
            return Result.failure(profileResult.exceptionOrThrow())
        }
        return Result.success(saveAccountToLocal(session, profileResult.getOrThrow(), platform))
    }

    private suspend fun saveAccountToLocal(
        session: CreateSessionResponse,
        profile: ProfileViewDetailed?,
        platform: BlogPlatform,
    ): BlueskyLoggedAccount {
        val loggedAccount = accountAdapter.createBlueskyAccount(
            profileViewDetailed = profile,
            createSessionResponse = session,
            platform = platform,
        )
        accountRepo.insert(loggedAccount)
        return loggedAccount
    }

    suspend fun logout(uri: FormalUri) {
        accountRepo.deleteByUri(uri.toString())
    }

    suspend fun getAllAccount(): List<BlueskyLoggedAccount> {
        return accountRepo.queryAll()
    }

    fun getAllAccountFlow(): Flow<List<BlueskyLoggedAccount>> {
        return accountRepo.queryAllFlow()
    }

    fun getAccountFlow(uri: FormalUri): Flow<BlueskyLoggedAccount> {
        return getAllAccountFlow().mapNotNull { it.firstOrNull { it.uri == uri } }
    }

    suspend fun getAccount(locator: PlatformLocator): BlueskyLoggedAccount? {
        val allAccount = getAllAccount()
        if (locator.accountUri != null) {
            allAccount.firstOrNull { it.uri == locator.accountUri }?.let { return it }
        }
        return allAccount.firstOrNull()
    }

    suspend fun updateAccountProfile(
        locator: PlatformLocator,
        profile: ProfileViewDetailed,
    ) {
        val account = getAccount(locator) ?: return
        if (account.did != profile.did.did) return
        val newAccount = accountAdapter.updateProfile(account, profile)
        accountRepo.updateAccount(account, newAccount)
    }

    suspend fun refreshAccountProfile(): List<AccountRefreshResult> {
        return accountRepo.queryAll().map { account ->
            val locator =
                PlatformLocator(accountUri = account.uri, baseUrl = account.platform.baseUrl)
            val result = clientManager.getClient(locator = locator).getProfile(account.did)
            if (result.isFailure) {
                AccountRefreshResult.Failure(account, result.exceptionOrThrow())
            } else {
                val newAccount = accountAdapter.updateProfile(account, result.getOrThrow())
                accountRepo.updateAccount(account, newAccount)
                AccountRefreshResult.Success(newAccount)
            }
        }
    }

    private suspend fun BlueskyClient.getProfile(did: String): Result<ProfileViewDetailed> {
        return this.getProfileCatching(GetProfileQueryParams(Did(did)))
    }
}
