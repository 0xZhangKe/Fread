package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class LoginToBskyUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val accountManager: BlueskyLoggedAccountManager,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        username: String,
        password: String,
    ): Result<BlueskyLoggedAccount> {
        val role = IdentityRole(baseUrl = baseUrl, accountUri = null)
        val client = clientManager.getClient(role)
        return accountManager.login(client, username, password)
    }
}
