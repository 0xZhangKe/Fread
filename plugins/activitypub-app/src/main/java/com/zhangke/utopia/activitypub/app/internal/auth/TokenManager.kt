package com.zhangke.utopia.activitypub.app.internal.auth

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {

    private val baseUrlToToken = mutableMapOf<FormalBaseUrl, ActivityPubTokenEntity>()

    fun getToken(baseUrl: FormalBaseUrl): ActivityPubTokenEntity? {
        return baseUrlToToken[baseUrl]
    }

    fun setToken(baseUrl: FormalBaseUrl, token: ActivityPubTokenEntity) {
        baseUrlToToken[baseUrl] = token
    }
}
