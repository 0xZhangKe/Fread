package com.zhangke.fread.activitypub.app.internal.uri

import com.zhangke.fread.status.uri.FormalUri

const val ACTIVITY_PUB_HOST = "activitypub.com"

fun createActivityPubUri(path: String, queries: Map<String, String>): FormalUri {
    return FormalUri.create(
        host = ACTIVITY_PUB_HOST,
        path = path,
        queries = queries,
    )
}
