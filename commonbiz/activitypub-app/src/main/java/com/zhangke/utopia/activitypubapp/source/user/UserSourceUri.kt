package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.utopia.activitypubapp.buildActivityPubSourceUri
import com.zhangke.utopia.activitypubapp.requireActivityPubUri
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import com.zhangke.utopia.status_provider.StatusSourceUri

private const val USER_PATH = "/user"

internal fun buildUserSourceUri(webFinger: WebFinger): StatusSourceUri {
    return buildActivityPubSourceUri(USER_PATH, webFinger.toString())
}

internal fun getUserWebFinger(uri: StatusSourceUri): WebFinger? {
    uri.requireActivityPubUri()
    if (uri.query.isEmpty()) return null
    val path = uri.path
    if (path != USER_PATH) return null
    return WebFinger.create(uri.query)
}