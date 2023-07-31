package com.zhangke.utopia.status.uri

import com.zhangke.framework.utils.uriString

class StatusProviderUri private constructor(
    val host: String,
    /**
     * format like "/xxx"
     */
    val path: String,
    val queries: Map<String, String>,
) {

    override fun toString(): String {
        return statusProviderUriString(host, path, queries)
    }

    companion object {

        const val SCHEME = "utopiaapp"

        fun build(
            host: String,
            path: String,
            queries: Map<String, String>,
        ): StatusProviderUri {
            if (host.isEmpty()) throw IllegalArgumentException("host must not empty")
            if (path.isEmpty()) throw IllegalArgumentException("path must not empty")
            val fixedPath = fixPath(path)
            return StatusProviderUri(host, fixedPath, queries)
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

        fun create(uri: String): StatusProviderUri? {
            val (host, path, queries) = StatusProviderUriParser().parse(uri) ?: return null
            return StatusProviderUri(host, path, queries)
        }
    }
}

fun statusProviderUriString(
    host: String,
    path: String,
    queries: Map<String, String>,
): String {
    return uriString(
        scheme = StatusProviderUri.SCHEME,
        host = host,
        path = path,
        queries = queries,
    )
}
