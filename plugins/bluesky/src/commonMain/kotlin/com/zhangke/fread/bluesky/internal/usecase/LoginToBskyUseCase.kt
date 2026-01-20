package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager

class LoginToBskyUseCase(
    private val accountManager: BlueskyLoggedAccountManager,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        identifier: String,
        password: String,
        factorToken: String? = null,
    ): Result<BlueskyLoggedAccount> {
        return accountManager.login(baseUrl, identifier, password, factorToken)
    }
}
