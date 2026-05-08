package com.zhangke.fread.common.repo

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.utils.LinkPreviewInfo
import com.zhangke.framework.utils.LinkPreviewUtils
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodeURLParameter
import io.ktor.http.isSuccess
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable

class LinkPreviewCardRepo {

    private val urlToInfoMap = mutableMapOf<String, LinkPreviewInfo>()

    suspend fun fetchPreviewInfo(url: String): Result<LinkPreviewInfo> {
        urlToInfoMap[url]?.let { return Result.success(it) }
        val target = ensureScheme(url)
        val cardyb = withTimeoutOrNull(FETCH_TIMEOUT_MS) {
            runCatching { fetchViaBluesky(target) }.getOrNull()
        }
        if (cardyb != null) {
            urlToInfoMap[url] = cardyb
            return Result.success(cardyb)
        }
        val local = withTimeoutOrNull(FETCH_TIMEOUT_MS) {
            runCatching { fetchFromOpenGraph(target) }.getOrNull()
        }
        if (local != null) {
            urlToInfoMap[url] = local
            return Result.success(local)
        }
        return Result.failure(IllegalStateException("Failed to fetch link preview info"))
    }

    private suspend fun fetchViaBluesky(url: String): LinkPreviewInfo? {
        val response = sharedHttpClient.get(
            "https://cardyb.bsky.app/v1/extract?url=${url.encodeURLParameter()}"
        ) {
            accept(ContentType.Application.Json)
        }
        if (!response.status.isSuccess()) return null
        val payload = response.body<CardybResponse>()
        if (!payload.error.isNullOrEmpty()) return null
        val title = payload.title?.takeIf { it.isNotBlank() } ?: return null
        return LinkPreviewInfo(
            title = title,
            description = payload.description?.takeIf { it.isNotBlank() },
            image = payload.image?.takeIf { it.isNotBlank() },
            url = payload.url?.takeIf { it.isNotBlank() } ?: url,
            siteName = null,
        )
    }

    private suspend fun fetchFromOpenGraph(url: String): LinkPreviewInfo? {
        val html = sharedHttpClient.get(url) {
            header(HttpHeaders.UserAgent, BROWSER_USER_AGENT)
            accept(ContentType.Text.Html)
            accept(ContentType.Application.Xml)
        }.bodyAsText()
        return LinkPreviewUtils.fetchPreviewInfo(url, html)
    }

    private fun ensureScheme(url: String): String {
        return if (url.startsWith("http://", ignoreCase = true) ||
            url.startsWith("https://", ignoreCase = true)
        ) {
            url
        } else {
            "https://$url"
        }
    }

    @Serializable
    private data class CardybResponse(
        val error: String? = null,
        val url: String? = null,
        val title: String? = null,
        val description: String? = null,
        val image: String? = null,
    )

    companion object {
        private const val FETCH_TIMEOUT_MS = 15_000L
        private const val BROWSER_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }
}
