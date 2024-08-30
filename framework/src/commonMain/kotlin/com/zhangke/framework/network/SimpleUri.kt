package com.zhangke.framework.network

import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.framework.utils.uriString

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
                uri.toPlatformUri()
            } catch (e: Throwable) {
                null
            } ?: return null
            val scheme = formalUri.scheme
            val host = formalUri.host
            val path = formalUri.path
            val queries = formalUri.getQueryParameterNames().associateWith {
                formalUri.getQueryParameter(it).orEmpty()
            }
            return SimpleUri(scheme, host, path, queries)
        }
    }
}
