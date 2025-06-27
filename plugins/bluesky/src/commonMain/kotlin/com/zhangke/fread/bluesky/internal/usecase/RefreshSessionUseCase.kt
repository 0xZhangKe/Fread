package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.fread.bluesky.BlueskyAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class RefreshSessionUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val accountManager: BlueskyAccountManager,
) {

    suspend operator fun invoke() {
        accountManager.getAllLoggedAccount()
            .map { PlatformLocator(accountUri = it.uri, baseUrl = it.platform.baseUrl) }
            .map { it to clientManager.getClient(it) }
            .forEach { (role, client) ->
                client.refreshSessionCatching()
                    .onSuccess { clientManager.updateNewSession(role, it) }
            }
    }
}
