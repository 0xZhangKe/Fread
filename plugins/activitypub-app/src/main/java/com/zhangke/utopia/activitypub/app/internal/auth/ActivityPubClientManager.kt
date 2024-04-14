package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.common.usecase.GetDefaultBaseUrlUseCase
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityPubClientManager @Inject constructor(
    private val loggedAccountProvider: LoggedAccountProvider,
    private val getDefaultBaseUrl: GetDefaultBaseUrlUseCase,
    private val userUriTransformer: UserUriTransformer,
) {

    private val cachedClient = mutableMapOf<IdentityRole, ActivityPubClient>()

    fun getClient(role: IdentityRole): ActivityPubClient {
        cachedClient[role]?.let { return it }
        var baseUrl: FormalBaseUrl? = null
        if (role.accountUri != null) {
            baseUrl = loggedAccountProvider.getAccount(role.accountUri!!)?.platform?.baseUrl
            if (baseUrl == null) {
                baseUrl = userUriTransformer.parse(role.accountUri!!)?.baseUrl
            }
        }
        if (baseUrl == null && role.baseUrl != null) {
            baseUrl = role.baseUrl
        }
        if (baseUrl == null) {
            baseUrl = getDefaultBaseUrl()
        }
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
            httpClient = GlobalOkHttpClient.client,
            gson = globalGson,
            tokenProvider = tokenProvider,
            onAuthorizeFailed = {

            },
        )
    }
}
