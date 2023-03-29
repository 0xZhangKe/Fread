package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.buildActivityPubSourceUri
import com.zhangke.utopia.activitypubapp.requireActivityPubUri
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status.source.StatusSourceUri

private const val USER_PATH = "/user"

internal fun buildUserSourceUri(webFinger: WebFinger): StatusSourceUri {
    return buildActivityPubSourceUri(USER_PATH, webFinger.toString())
}

internal fun StatusSourceUri.isUserSource(): Boolean {
    return getUserWebFinger() != null
}

internal fun StatusSourceUri.getUserWebFinger(): WebFinger? {
    requireActivityPubUri()
    if (query.isEmpty()) return null
    if (path != USER_PATH) return null
    return WebFinger.create(query)
}