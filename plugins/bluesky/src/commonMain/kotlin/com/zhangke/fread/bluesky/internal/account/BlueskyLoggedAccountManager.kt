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
import com.zhangke.fread.status.model.IdentityRole
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
        client: BlueskyClient,
        username: String,
        password: String,
    ): Result<BlueskyLoggedAccount> {
        val sessionResult =
            client.createSessionCatching(CreateSessionRequest(username, password))
        if (sessionResult.isFailure) {
            return Result.failure(sessionResult.exceptionOrThrow())
        }
        val session = sessionResult.getOrThrow()
        val platform = platformRepo.getPlatform(client.baseUrl)
        saveAccountToLocal(session, null, platform)
        val profileResult = client.getProfile(session.did.did)
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

    fun getAccountFlow(baseUrl: FormalBaseUrl): Flow<BlueskyLoggedAccount> {
        return getAllAccountFlow().mapNotNull { it.firstOrNull { it.platform.baseUrl == baseUrl } }
    }

    suspend fun getAccount(role: IdentityRole): BlueskyLoggedAccount? {
        if (role.nonRole) return null
        val allAccount = getAllAccount()
        if (role.accountUri != null) {
            allAccount.firstOrNull { it.uri == role.accountUri }?.let { return it }
        }
        return if (role.baseUrl != null) {
            allAccount.firstOrNull { it.platform.baseUrl == role.baseUrl }
        } else {
            null
        }
    }

    suspend fun refreshAccountProfile() {
        accountRepo.queryAll().forEach { account ->
            val role = IdentityRole(accountUri = account.uri, baseUrl = account.platform.baseUrl)
            clientManager.getClient(role).getProfile(account.did).getOrNull()?.let { profile ->
                val newAccount = accountAdapter.updateProfile(account, profile)
                accountRepo.updateAccount(account, newAccount)
            }
        }
    }

    private suspend fun BlueskyClient.getProfile(did: String): Result<ProfileViewDetailed> {
        return this.getProfileCatching(GetProfileQueryParams(Did(did)))
    }
}
