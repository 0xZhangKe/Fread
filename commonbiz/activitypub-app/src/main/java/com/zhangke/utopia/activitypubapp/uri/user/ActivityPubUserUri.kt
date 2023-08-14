package com.zhangke.utopia.activitypubapp.uri.user

import com.zhangke.utopia.activitypubapp.uri.ActivityPubUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubUserUri private constructor(
    val userId: String,
    val finger: WebFinger,
    queries: Map<String, String>
) : ActivityPubUri(PATH, queries) {

    companion object {

        const val PATH = "/user"

        internal const val QUERY_ID = "userId"
        internal const val QUERY_FINGER = "finger"

        const val baseUrl: String = "${StatusProviderUri.SCHEME}://$HOST$PATH"

        fun create(userId: String, webFinger: WebFinger): ActivityPubUserUri {
            val queries = mapOf(QUERY_ID to userId, QUERY_FINGER to webFinger.toString())
            return ActivityPubUserUri(userId, webFinger, queries)
        }
    }
}
