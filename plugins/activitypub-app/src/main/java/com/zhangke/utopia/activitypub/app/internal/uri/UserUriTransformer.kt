package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class UserUriTransformer @Inject constructor() {

    companion object {

        private const val QUERY_FINGER = "finger"
    }

    fun parse(uri: FormalUri): UserUriInsights? {
        if (!uri.isActivityPubUri) return null
        if (uri.path != ActivityPubUriPath.USER) return null
        val webFinger = uri.queries[QUERY_FINGER]?.let { WebFinger.create(it) } ?: return null
        return UserUriInsights(
            uri = uri,
            webFinger = webFinger,
        )
    }

    fun build(webFinger: WebFinger): FormalUri {
        val queries = mutableMapOf<String, String>()
        queries[QUERY_FINGER] = webFinger.toString()
        return createActivityPubUri(
            path = ActivityPubUriPath.USER,
            queries = queries,
        )
    }
}
