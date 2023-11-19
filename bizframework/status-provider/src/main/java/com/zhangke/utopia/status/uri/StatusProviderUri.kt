package com.zhangke.utopia.status.uri

import com.zhangke.framework.utils.uriString

open class StatusProviderUri(
    val host: String,
    /**
     * format like "/xxx"
     */
    path: String,
    val queries: Map<String, String>,
) {

    val scheme = SCHEME

    val path = fixPath(path)

    override fun toString(): String {
        return uriString(
            scheme = scheme,
            host = host,
            path = path,
            queries = queries,
        )
    }

    private fun fixPath(path: String): String {
        var newPath = path
        if (!newPath.startsWith('/')) {
            newPath = "/$newPath"
        }
        if (newPath.endsWith('/')) {
            newPath = newPath.substring(0, newPath.length - 1)
        }
        return newPath
    }

    companion object {

        const val SCHEME = "utopiaapp"

        fun from(uri: String): StatusProviderUri? {
            val (host, path, queries) = StatusProviderUriParser().parse(uri) ?: return null
            return StatusProviderUri(host, path, queries)
        }
    }
}
