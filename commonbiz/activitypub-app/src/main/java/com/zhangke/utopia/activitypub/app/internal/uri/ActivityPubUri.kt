package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.StatusProviderUri

open class ActivityPubUri protected constructor(
    path: String,
    queries: Map<String, String>
) : StatusProviderUri(host = HOST, path = path, queries = queries) {

    companion object {

        const val HOST = "activitypub.com"
    }
}
