package com.zhangke.fread.signal.archive.screens.add

import io.ktor.http.URLProtocol
import io.ktor.http.Url

/** Converts a GitHub repository or static reading endpoint to its data directory. */
internal fun signalArchiveBaseUrl(input: String): String? {
    val url = try {
        Url(input.trim())
    } catch (_: IllegalArgumentException) {
        return null
    }
    if (url.protocol != URLProtocol.HTTP && url.protocol != URLProtocol.HTTPS) return null
    if (url.host.isBlank() || !input.trim().contains("://")) return null
    if (!url.parameters.isEmpty() || url.fragment.isNotEmpty()) return null
    val path = url.encodedPath.trim('/')
    if (url.host.equals("github.com", ignoreCase = true)) {
        val parts = path.split('/')
        if (parts.size != 2 || parts.any { it.isBlank() }) return null
        val repository = parts[1].removeSuffix(".git")
        if (repository.isBlank()) return null
        return "https://raw.githubusercontent.com/${parts[0]}/$repository/archive/"
    }
    val base = url.toString().trimEnd('/')
    return if (path.endsWith("rendered.json")) {
        base.removeSuffix("rendered.json")
    } else {
        "$base/"
    }
}
