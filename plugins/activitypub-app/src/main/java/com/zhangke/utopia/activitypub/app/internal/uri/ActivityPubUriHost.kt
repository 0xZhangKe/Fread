package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.StatusProviderUri

const val ACTIVITY_PUB_HOST = "activitypub.com"

fun createActivityPubUri(path: String, queries: Map<String, String>): StatusProviderUri {
    return StatusProviderUri.create(
        host = ACTIVITY_PUB_HOST,
        path = path,
        queries = queries,
    )
}
