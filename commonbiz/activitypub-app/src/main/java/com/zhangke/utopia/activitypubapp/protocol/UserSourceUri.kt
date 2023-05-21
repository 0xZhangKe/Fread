package com.zhangke.utopia.activitypubapp.protocol

import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusProviderUri

// /user?

private const val userPath = "/user"

internal fun buildUserSourceUri(webFinger: WebFinger): StatusProviderUri {
    return buildActivityPubSourceUri(userPath, mapOf("finger" to webFinger.toString()))
}

internal fun StatusProviderUri.isUserSource(): Boolean {
    return path == userPath
}

internal fun StatusProviderUri.getUserWebFinger(): WebFinger? {
    requireActivityPubUri()
    if (path != userPath) return null
    val finger = queries["finger"]
    if (finger.isNullOrEmpty()) return null
    return WebFinger.create(finger)
}
