package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider

class GetDefaultBaseUrlUseCase (
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    companion object {

        private val defaultBaseUrl = FormalBaseUrl.Companion.parse("https://mastodon.online")!!
    }

    operator fun invoke(): FormalBaseUrl {
        loggedAccountProvider.getAllAccounts().firstOrNull()?.let {
            return it.platform.baseUrl
        }
        return defaultBaseUrl
    }
}