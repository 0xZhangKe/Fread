package com.zhangke.framework.network

import java.net.URI

data class SimpleUri(
    val scheme: String?,
    val host: String?,
    val path: String?,
    val queries: Map<String, String>,
) {

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
            formalUri.query?.let { query ->
                query.split("&").forEach { pair ->
                    val (key, value) = pair.split("=")
                    queries[key] = value
                }
            }
            return SimpleUri(scheme, host, path, queries)
        }
    }
}
