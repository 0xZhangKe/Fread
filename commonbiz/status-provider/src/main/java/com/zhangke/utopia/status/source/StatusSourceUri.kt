package com.zhangke.utopia.status.source

import com.zhangke.utopia.protocol.UtopiaScheme

class StatusSourceUri private constructor(
    val host: String,
    val path: String,
    val query: String,
) {

    override fun toString(): String {
        // statussource://activitypub.com/user/name?query=123
        return with(StringBuilder()) {
            append(STATUS_SOURCE_SCHEME)
            append(SCHEME_DIVIDER)
            append(host)
            append(path)
            append(QUERY_DIVIDER)
            append(QUERY_NAME)
            append('=')
            append(query)
        }.toString()
    }

    companion object {

        private const val STATUS_SOURCE_SCHEME = UtopiaScheme
        private const val SCHEME_DIVIDER = "://"
        private const val PATH_DIVIDER = "/"
        private const val QUERY_DIVIDER = "?"
        private const val QUERY_NAME = "query"

        fun build(
            host: String,
            path: String,
            query: String
        ): StatusSourceUri {
            // statussource://activitypub.com/user/name?query=123
            if (host.isEmpty()) throw IllegalArgumentException("host must not empty")
            if (path.isEmpty()) throw IllegalArgumentException("path must not empty")
            var newPath = path
            if (!newPath.startsWith('/')) {
                newPath = "/$newPath"
            }
            if (newPath.endsWith('/')) {
                newPath = newPath.substring(0, newPath.length - 1)
            }
            return StatusSourceUri(host, newPath, query)
        }

        fun create(uri: String): StatusSourceUri? {
            val scheme = findScheme(uri)
            if (scheme != STATUS_SOURCE_SCHEME) return null
            val host = findHost(uri)
            if (host.isNullOrEmpty()) return null
            val path = findPath(uri)
            if (path.isNullOrEmpty()) return null
            val query = findQuery(uri) ?: return null
            return StatusSourceUri(host, path, query)
        }

        private fun findScheme(uri: String): String? {
            return uri.split(SCHEME_DIVIDER)
                .takeIf { it.size == 2 }
                ?.getOrNull(0)
        }

        private fun findHost(uri: String): String? {
            val scheme = findScheme(uri) ?: return null
            return uri.removePrefix(scheme + SCHEME_DIVIDER)
                .split(PATH_DIVIDER)
                .takeIf { it.size > 1 }
                ?.getOrNull(0)
        }

        private fun findPath(uri: String): String? {
            val scheme = findScheme(uri) ?: return null
            val host = findHost(uri) ?: return null
            // user/name?query=123
            return uri.removePrefix(scheme + SCHEME_DIVIDER + host + PATH_DIVIDER)
                .split(QUERY_DIVIDER)
                .takeIf { it.size > 1 }
                ?.getOrNull(0)
                ?.takeIf { it.isNotEmpty() }
                ?.let { PATH_DIVIDER + it }
        }

        private fun findQuery(uri: String): String? {
            val queryPart = uri.split(QUERY_DIVIDER)
                .takeIf { it.size == 2 }
                ?.getOrNull(1) ?: return null
            if (!queryPart.startsWith("$QUERY_NAME=")) return null
            return queryPart.removePrefix("$QUERY_NAME=")
        }
    }
}