package com.zhangke.framework.utils

import android.net.Uri

fun uriString(
    scheme: String,
    host: String,
    path: String,
    queries: Map<String, String>,
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
            .joinToString(prefix = "?", separator = "&") { "${it.key}=${Uri.encode(it.value)}" }
        builder.append(query)
    }
    return builder.toString()
}
