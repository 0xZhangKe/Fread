package com.zhangke.utopia.status.uri

import com.zhangke.framework.utils.uriString
import kotlinx.serialization.Serializable

@Serializable
class FormalUri constructor(
    val host: String,
    /**
     * format like "/xxx"
     */
    private val rawPath: String,
    val queries: Map<String, String>,
) {

    @Suppress("MemberVisibilityCanBePrivate")
    val scheme = SCHEME

    val path = fixPath(rawPath)

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

        fun create(host: String, path: String, queries: Map<String, String>): FormalUri {
            return FormalUri(
                host = host,
                rawPath = path,
                queries = queries,
            )
        }

        fun from(uri: String): FormalUri? {
            val (host, path, queries) = FormalUriParser().parse(uri) ?: return null
            return FormalUri(host, path, queries)
        }
    }
}
