package com.zhangke.utopia.activitypub.app.internal.utils

import com.zhangke.framework.network.HttpScheme

internal fun String.toBaseUrl(): String {
    if (hasProtocol()) return this
    return "${HttpScheme.HTTPS}$this"
}

internal fun String.toDomain(): String {
    return if (hasProtocol()) {
        this.removePrefix(HttpScheme.HTTP).removePrefix(HttpScheme.HTTPS)
    } else {
        this
    }
}

private fun String.hasProtocol(): Boolean {
    return startsWith(HttpScheme.HTTP, true) || startsWith(HttpScheme.HTTPS, true)
}
