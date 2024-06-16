package com.zhangke.fread.rss.internal.uri

import com.zhangke.fread.status.uri.FormalUri

const val RSS_HOST = "rss.com"

fun createRssUri(path: String, queries: Map<String, String>): FormalUri {
    return FormalUri.create(
        host = RSS_HOST,
        path = path,
        queries = queries,
    )
}

val FormalUri.isRssUri: Boolean get() = host == RSS_HOST
