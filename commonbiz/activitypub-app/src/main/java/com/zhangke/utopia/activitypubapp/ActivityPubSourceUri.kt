package com.zhangke.utopia.activitypubapp

import com.zhangke.utopia.status_provider.StatusSourceUri

private const val ACTIVITY_PUB_HOST = "activitypub.com"

internal fun buildActivityPubSourceUri(
    path: String,
    query: String
): StatusSourceUri {
    return StatusSourceUri.build(ACTIVITY_PUB_HOST, path, query)
}

internal fun StatusSourceUri.isActivityPubUri(): Boolean {
    return host == ACTIVITY_PUB_HOST
}

internal fun StatusSourceUri.requireActivityPubUri() {
    if (!isActivityPubUri()) throw IllegalArgumentException("Uri must be a ActivityPubUri!")
}