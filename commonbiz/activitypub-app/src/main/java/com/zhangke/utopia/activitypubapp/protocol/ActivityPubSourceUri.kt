package com.zhangke.utopia.activitypubapp.protocol

import com.zhangke.utopia.status.utils.StatusProviderUri

private const val ACTIVITY_PUB_HOST = "activitypub.com"

internal fun buildActivityPubSourceUri(
    path: String,
    queries: Map<String, String>,
): StatusProviderUri {
    return StatusProviderUri.build(ACTIVITY_PUB_HOST, path, queries)
}

internal fun StatusProviderUri.isActivityPubUri(): Boolean {
    return host == ACTIVITY_PUB_HOST
}

internal fun StatusProviderUri.requireActivityPubUri() {
    if (!isActivityPubUri()) throw IllegalArgumentException("Uri must be a ActivityPubUri!")
}
