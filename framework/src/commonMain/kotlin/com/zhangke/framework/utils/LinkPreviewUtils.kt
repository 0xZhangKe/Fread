package com.zhangke.framework.utils

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document

object LinkPreviewUtils {

    fun fetchPreviewInfo(url: String, html: String): LinkPreviewInfo? {
        val document = runCatching { Ksoup.parse(html) }.getOrNull() ?: return null
        val pageUrl = resolveUrl(
            rawUrl = document.selectFirst("base[href]")?.attr("href"),
            articleUrl = url,
        ) ?: url
        val previewUrl = resolveUrl(
            rawUrl = firstNotBlank(
                document.metaContent("meta[property=og:url]"),
                document.metaContent("meta[name=og:url]"),
                document.linkHref("link[rel=canonical]"),
                document.linkHref("link[rel=alternate][hreflang=x-default]"),
            ),
            articleUrl = pageUrl,
        ) ?: url
        val title = firstNotBlank(
            document.metaContent("meta[property=og:title]"),
            document.metaContent("meta[name=og:title]"),
            document.metaContent("meta[name=twitter:title]"),
            document.metaContent("meta[property=twitter:title]"),
            document.title().normalizeText(),
            document.selectFirst("h1")?.text().normalizeText(),
        ) ?: return null
        val description = firstNotBlank(
            document.metaContent("meta[property=og:description]"),
            document.metaContent("meta[name=og:description]"),
            document.metaContent("meta[name=twitter:description]"),
            document.metaContent("meta[property=twitter:description]"),
            document.metaContent("meta[name=description]"),
            document.metaContent("meta[property=description]"),
        )
        val image = resolveUrl(
            rawUrl = firstNotBlank(
                document.metaContent("meta[property=og:image]"),
                document.metaContent("meta[property=og:image:url]"),
                document.metaContent("meta[name=twitter:image]"),
                document.metaContent("meta[property=twitter:image]"),
                document.metaContent("meta[itemprop=image]"),
                document.linkHref("link[rel=image_src]"),
                document.linkHref("link[rel=apple-touch-icon]"),
            ),
            articleUrl = pageUrl,
        )
        val siteName = firstNotBlank(
            document.metaContent("meta[property=og:site_name]"),
            document.metaContent("meta[name=application-name]"),
            document.metaContent("meta[name=apple-mobile-web-app-title]"),
        )
        return LinkPreviewInfo(
            title = title,
            description = description,
            image = image,
            url = previewUrl,
            siteName = siteName,
        )
    }

    private fun Document.metaContent(selector: String): String? {
        return selectFirst(selector)
            ?.attr("content")
            .normalizeText()
    }

    private fun Document.linkHref(selector: String): String? {
        return selectFirst(selector)
            ?.attr("href")
            .normalizeText()
    }

    private fun String?.normalizeText(): String? {
        return this
            ?.replace(whitespaceRegex, " ")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    private fun firstNotBlank(vararg values: String?): String? {
        return values.firstNotNullOfOrNull { it.normalizeText() }
    }

    private fun resolveUrl(rawUrl: String?, articleUrl: String?): String? {
        val value = rawUrl.normalizeText() ?: return null
        if (value.startsWith("data:", ignoreCase = true)) return null
        if (value.startsWith("blob:", ignoreCase = true)) return null
        if (value.startsWith("javascript:", ignoreCase = true)) return null
        if (value.startsWith("http://", ignoreCase = true) ||
            value.startsWith("https://", ignoreCase = true)
        ) {
            return value
        }
        if (value.startsWith("//")) {
            val scheme = articleUrl
                ?.substringBefore("://", "")
                ?.takeIf { it.isNotBlank() }
                ?: "https"
            return "$scheme:$value"
        }
        val baseUrl = articleUrl
            ?.takeIf {
                it.startsWith("http://", ignoreCase = true) ||
                        it.startsWith("https://", ignoreCase = true)
            }
            ?: return null
        val origin = baseUrl.origin() ?: return null
        if (value.startsWith("/")) {
            return origin + normalizePath(value)
        }
        val sanitizedBase = baseUrl.substringBefore('#').substringBefore('?')
        val directory = if (sanitizedBase.endsWith("/")) {
            sanitizedBase
        } else {
            sanitizedBase.substringBeforeLast("/", "$origin/")
        }
        val relativePath = directory.substringAfter(origin, "/").trimEnd('/')
        val normalizedPath = normalizePath("$relativePath/$value")
        return origin + normalizedPath
    }

    private fun String.origin(): String? {
        val schemeIndex = indexOf("://")
        if (schemeIndex <= 0) return null
        val hostStart = schemeIndex + 3
        val hostEnd = indexOf('/', startIndex = hostStart).takeIf { it >= 0 } ?: length
        return substring(0, hostEnd)
    }

    private fun normalizePath(pathWithQuery: String): String {
        val fragment = pathWithQuery.substringAfter('#', "")
        val pathWithoutFragment = pathWithQuery.substringBefore('#')
        val query = pathWithoutFragment.substringAfter('?', "")
        val rawPath = pathWithoutFragment.substringBefore('?')
        val stack = mutableListOf<String>()
        rawPath.split('/').forEach { segment ->
            when (segment) {
                "", "." -> Unit
                ".." -> if (stack.isNotEmpty()) {
                    stack.removeAt(stack.lastIndex)
                }

                else -> stack += segment
            }
        }
        val normalizedPath = "/" + stack.joinToString("/")
        return buildString {
            append(normalizedPath)
            if (query.isNotEmpty()) {
                append('?')
                append(query)
            }
            if (fragment.isNotEmpty()) {
                append('#')
                append(fragment)
            }
        }
    }
}

private val whitespaceRegex = "\\s+".toRegex()

data class LinkPreviewInfo(
    val title: String,
    val description: String?,
    val image: String?,
    val url: String,
    val siteName: String?,
)
