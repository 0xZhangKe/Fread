package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.http.createHttpClientEngine
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class ActivityPubClientManager @Inject constructor(
    private val resolveBaseUrl: ResolveBaseUrlUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    private val cachedClient = mutableMapOf<IdentityRole, ActivityPubClient>()

    private val httpClientEngine by lazy {
        createHttpClientEngine()
    }

    fun clearCache() {
        cachedClient.clear()
    }

    fun getClient(role: IdentityRole): ActivityPubClient {
        cachedClient[role]?.let { return it }
        val baseUrl = resolveBaseUrl(role)
        return createClient(
            baseUrl = baseUrl,
            tokenProvider = {
                var token: ActivityPubTokenEntity? = null
                if (role.accountUri != null) {
                    token = loggedAccountProvider.getAccount(role.accountUri!!)?.token
                }
                if (token == null) {
                    token = loggedAccountProvider.getAccount(baseUrl)?.token
                }
                token
            },
        ).also {
            cachedClient[role] = it
        }
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
