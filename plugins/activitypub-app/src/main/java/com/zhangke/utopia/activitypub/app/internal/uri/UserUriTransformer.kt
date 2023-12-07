package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class UserUriTransformer @Inject constructor() {

    companion object {

        private const val QUERY_ID = "userId"
        private const val QUERY_FINGER = "finger"
    }

    fun parse(uri: StatusProviderUri): UserUriInsights? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.USER) return null
        val userId = uri.queries[QUERY_ID]?.takeIf { it.isNotEmpty() } ?: return null
        val webFinger = uri.queries[QUERY_FINGER]?.let { WebFinger.create(it) } ?: return null
        return UserUriInsights(
            uri = uri,
            userId = userId,
            webFinger = webFinger,
        )
    }

    fun build(userId: String, webFinger: WebFinger): StatusProviderUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_ID] = userId
        queries[QUERY_FINGER] = webFinger.toString()
        return createActivityPubUri(
            path = ActivityPubUriPath.USER,
            queries = queries,
        )
    }
}
