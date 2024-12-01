package com.zhangke.fread.bluesky.internal.uri

import com.zhangke.fread.status.uri.FormalUri

const val BLUESKY_HOST = "bluesky.social"

fun createBlueskyUri(path: String, queries: Map<String, String>): FormalUri {
    return FormalUri.create(
        host = BLUESKY_HOST,
        path = path,
        queries = queries,
    )
}

val FormalUri.isBlueskyUri: Boolean get() = host == BLUESKY_HOST
