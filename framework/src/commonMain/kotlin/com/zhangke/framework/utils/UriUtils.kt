package com.zhangke.framework.utils

import io.ktor.http.encodeURLPath

fun uriString(
    scheme: String,
    host: String,
    path: String,
    queries: Map<String, String>,
    encode: Boolean = true,
): String {
    val builder = StringBuilder()
    if (scheme.isNotEmpty()) {
        builder.append(scheme)
        builder.append("://")
    }
    builder.append(host)
    var fixedPath = path
    if (builder.endsWith("/") && path.startsWith("/")) {
        fixedPath = fixedPath.removePrefix("/")
    }
    if (fixedPath.endsWith("/") && queries.isNotEmpty()) {
        fixedPath = fixedPath.removeSuffix("/")
    }
    builder.append(fixedPath)
    if (queries.isNotEmpty()) {
        val query = queries.entries
            .joinToString(prefix = "?", separator = "&") {
                val value = if (encode) {
                    it.value.encodeURLPath()
                } else {
                    it.value
                }
                "${it.key}=$value"
            }
        builder.append(query)
    }
    return builder.toString()
}

fun String.decodeAsUri(): String {
    return encodeURLPath()
}
