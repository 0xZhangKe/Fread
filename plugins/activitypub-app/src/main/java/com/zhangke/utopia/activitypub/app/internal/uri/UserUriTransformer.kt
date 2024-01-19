package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class UserUriTransformer @Inject constructor() {

    companion object {

        private const val QUERY_FINGER = "finger"
        private const val QUERY_BASE_URL = "baseUrl"
    }

    fun parse(uri: FormalUri): UserUriInsights? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.USER) return null
        val webFinger = uri.queries[QUERY_FINGER]?.let { WebFinger.create(it) } ?: return null
        val baseUrl = uri.queries[QUERY_BASE_URL]?.let { FormalBaseUrl.parse(it) } ?: return null
        return UserUriInsights(
            uri = uri,
            webFinger = webFinger,
            baseUrl = baseUrl,
        )
    }

    fun build(
        webFinger: WebFinger,
        baseUrl: FormalBaseUrl,
    ): FormalUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_FINGER] = webFinger.toString()
        queries[QUERY_BASE_URL] = baseUrl.toString()
        return createActivityPubUri(
            path = ActivityPubUriPath.USER,
            queries = queries,
        )
    }
}
