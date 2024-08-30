package com.zhangke.fread.status.uri

import io.ktor.http.decodeURLQueryComponent

private val defaultUriDecoder: (String) -> String = { url ->
    url.decodeURLQueryComponent()
}

class FormalUriParser(
    val uriDecoder: (String) -> String = defaultUriDecoder,
) {

    fun parse(uriString: String): Triple<String, String, Map<String, String>>? {
        val fixedUriString = uriString.trim()
        val scheme = findScheme(fixedUriString)
        if (scheme != FormalUri.SCHEME) return null
        val host = findHost(fixedUriString)
        if (host.isNullOrEmpty()) return null
        val path = findPath(fixedUriString)
        if (path.isNullOrEmpty()) return null
        val queries = findQueries(fixedUriString)
        return Triple(host, path, queries)
    }

    private fun findScheme(uri: String): String? {
        return uri.split("://")
            .takeIf { it.size >= 2 }
            ?.getOrNull(0)
    }

    private fun findHost(uri: String): String? {
        val scheme = findScheme(uri) ?: return null
        return uri.removePrefix("$scheme://")
            .split("/")
            .getOrNull(0)
    }

    private fun findPath(uri: String): String? {
        val scheme = findScheme(uri) ?: return null
        val host = findHost(uri) ?: return null
        return uri.removePrefix("$scheme://$host/")
            .split('?')
            .getOrNull(0)
            ?.takeIf { it.isNotEmpty() }
            ?.let { "/$it" }
    }

    private fun findQueries(uri: String): Map<String, String> {
        val queryPart = uri.split('?').getOrNull(1)
            .takeIf { !it.isNullOrEmpty() } ?: return emptyMap()
        return queryPart.split('&')
            .associate {
                val array = it.split('=')
                array[0] to uriDecoder(array.getOrElse(1) { "" })
            }
    }
}
