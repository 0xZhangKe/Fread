package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class ActivityPubClientManager @Inject constructor(
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    private val cachedClient = mutableMapOf<PlatformLocator, ActivityPubClient>()

    private val httpClientEngine by lazy {
        createHttpClientEngine()
    }

    fun clearCache() {
        cachedClient.clear()
    }

    fun getClient(locator: PlatformLocator): ActivityPubClient {
        cachedClient[locator]?.let { return it }
        return createClient(
            baseUrl = locator.baseUrl,
            tokenProvider = {
                if (locator.accountUri != null) {
                    loggedAccountProvider.getAccount(locator.accountUri!!)?.token
                } else {
                    loggedAccountProvider.getAccount(locator.baseUrl)?.token
                }
            },
        ).also {
            cachedClient[locator] = it
        }
    }

    fun getClientNoAccount(baseUrl: FormalBaseUrl): ActivityPubClient {
        return createClient(
            baseUrl = baseUrl,
            tokenProvider = { null },
        )
    }

    private fun createClient(
        baseUrl: FormalBaseUrl,
        tokenProvider: () -> ActivityPubTokenEntity?,
    ): ActivityPubClient {
        return ActivityPubClient(
            baseUrl = "${baseUrl}/",
            engine = httpClientEngine,
            json = globalJson,
            tokenProvider = tokenProvider,
            onAuthorizeFailed = {

            },
        )
    }
}
