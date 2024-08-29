package com.zhangke.framework.network

import com.zhangke.framework.utils.uriString
import java.net.URI

data class SimpleUri(
    val scheme: String?,
    val host: String?,
    val path: String?,
    val queries: Map<String, String>,
) {

    override fun toString(): String {
        return uriString(
            scheme = scheme.orEmpty(),
            host = host.orEmpty(),
            path = path.orEmpty(),
            queries = queries,
        )
    }

    companion object {

        fun parse(uri: String): SimpleUri? {
            if (uri.isEmpty()) return null
            val formalUri = try {
                URI.create(uri)
            } catch (e: Throwable) {
                return null
            }
            val scheme = formalUri.scheme
            val host = formalUri.host
            val path = formalUri.path
            val queries = mutableMapOf<String, String>()
            formalUri.rawQuery?.let { query ->
                val queryGroups = if (query.contains("&")) {
                    query.split("&")
                } else {
                    listOf(query)
                }
                queryGroups.forEach { pair ->
                    pair.split("=")
                        .takeIf { it.size == 2 }
                        ?.let {
                            val (key, value) = it
                            queries[key] = value
                        }
                }
            }
            return SimpleUri(scheme, host, path, queries)
        }
    }
}
