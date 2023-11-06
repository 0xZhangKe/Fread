package com.zhangke.utopia.activitypub.app.internal.utils

private const val HTTP = "http://"
private const val HTTPS = "https://"

internal fun String.toBaseUrl(): String {
    if (hasProtocol()) return this
    return "${HTTPS}$this"
}

internal fun String.toDomain(): String {
    return if (hasProtocol()) {
        this.removePrefix(HTTP).removePrefix(HTTPS)
    } else {
        this
    }
}

private fun String.hasProtocol(): Boolean {
    return startsWith(HTTP, true) || startsWith(HTTPS, true)
}
