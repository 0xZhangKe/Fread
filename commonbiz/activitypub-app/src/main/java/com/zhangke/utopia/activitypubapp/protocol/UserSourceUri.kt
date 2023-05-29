package com.zhangke.utopia.activitypubapp.protocol

import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusProviderUri

private const val userPath = "/user"
private const val userIdQuery = "userId"

internal fun buildUserSourceUri(webFinger: WebFinger, userId: String): StatusProviderUri {
    return buildActivityPubSourceUri(
        userPath,
        mapOf(
            "finger" to webFinger.toString(),
            userIdQuery to userId,
        )
    )
}

internal fun StatusProviderUri.isUserSource(): Boolean {
    return path == userPath
}

internal fun StatusProviderUri.parseInfo(): Pair<WebFinger, String>? {
    requireActivityPubUri()
    if (path != userPath) return null
    val finger = queries["finger"]?.let { WebFinger.create(it) } ?: return null
    val userId = queries[userIdQuery] ?: return null
    return finger to userId
}
