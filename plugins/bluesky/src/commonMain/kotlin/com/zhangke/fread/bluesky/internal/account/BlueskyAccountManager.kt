package com.zhangke.fread.bluesky.internal.account

import com.atproto.server.CreateSessionRequest
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.LoginToBskyUseCase
import com.zhangke.fread.bluesky.internal.utils.toResult
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class BlueskyAccountManager @Inject constructor() {

    suspend fun login(
        clientManager: BlueskyClientManager,
        hosting: String,
        username: String,
        password: String,
    ): Result<Unit> {
        val baseUrl = FormalBaseUrl.parse(hosting)
        if (baseUrl == null) {
            return Result.failure(IllegalArgumentException("Invalid hosting"))
        }
        val role = IdentityRole(baseUrl = baseUrl, accountUri = null)
        val client = clientManager.getClient(role)
//        client.getProfile()
        return client.createSession(CreateSessionRequest(username, password))
            .toResult()
            .map {  }
    }

    suspend fun getAccount(role: IdentityRole): BlueskyLoggedAccount? {

        return null
    }
}
