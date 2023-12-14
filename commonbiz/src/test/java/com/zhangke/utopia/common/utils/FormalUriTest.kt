package com.zhangke.utopia.common.utils

import com.zhangke.utopia.status.uri.FormalUri

const val ACTIVITY_PUB_HOST = "activitypub.com"

private fun createActivityPubUri(path: String, queries: Map<String, String>): FormalUri {
    return FormalUri.create(
        host = ACTIVITY_PUB_HOST,
        path = path,
        queries = queries,
    )
}

fun createActivityPubUserUri(
    userId: String = "1",
    finger: String = "@AtomZ@m.cmx.im"
): FormalUri = createActivityPubUri(
    path = "/user",
    queries = mapOf(
        "userId" to userId,
        "finger" to finger,
    ),
)
