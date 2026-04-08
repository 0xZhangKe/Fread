package com.zhangke.fread.rss.internal.adapter

import com.zhangke.framework.security.Md5
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType

internal object RssBlogMediaExtractor {

    private val imageTagRegex = Regex("""<img\b[^>]*>""", setOf(RegexOption.IGNORE_CASE))
    private val srcSetSplitRegex = Regex("""\s+""")
    private val imageSourceAttributes = listOf(
        "src",
        "data-src",
        "data-original",
        "data-lazy-src",
        "data-actualsrc",
        "data-srcset",
        "srcset",
    )
    private val iconKeywordRegex = Regex(
        pattern = """avatar|icon|logo|emoji|favicon|gravatar|sprite|badge|profile|author|masthead|publisher""",
        options = setOf(RegexOption.IGNORE_CASE),
    )

    fun extract(
        itemId: String,
        articleUrl: String?,
        contentHtml: String?,
        descriptionHtml: String?,
        fallbackImageUrl: String?,
    ): List<BlogMedia> {
        val medias = linkedMapOf<String, BlogMedia>()
        listOfNotNull(contentHtml, descriptionHtml)
            .forEach { html ->
                extractFromHtml(itemId, html, articleUrl).forEach { media ->
                    if (!medias.containsKey(media.url)) {
                        medias[media.url] = media
                    }
                }
            }
        if (medias.isEmpty()) {
            createMedia(
                itemId = itemId,
                url = normalizeUrl(fallbackImageUrl, articleUrl),
                description = null,
                tagSnapshot = fallbackImageUrl.orEmpty(),
                width = null,
                height = null,
            )?.let { medias[it.url] = it }
        }
        return medias.values.toList()
    }

    private fun extractFromHtml(
        itemId: String,
        html: String,
        articleUrl: String?,
    ): List<BlogMedia> {
        val medias = linkedMapOf<String, BlogMedia>()
        imageTagRegex.findAll(html).forEach { match ->
            val tag = match.value
            val source = findImageSource(tag)
            createMedia(
                itemId = itemId,
                url = normalizeUrl(source, articleUrl),
                description = extractAttributeValue(tag, "alt"),
                tagSnapshot = tag,
                width = extractDimension(tag, "width"),
                height = extractDimension(tag, "height"),
            )?.let { media ->
                if (!medias.containsKey(media.url)) {
                    medias[media.url] = media
                }
            }
        }
        return medias.values.toList()
    }

    private fun findImageSource(tag: String): String? {
        imageSourceAttributes.forEach { attribute ->
            val value = extractAttributeValue(tag, attribute) ?: return@forEach
            if (attribute.endsWith("srcset", ignoreCase = true)) {
                val srcSetValue = pickFromSrcSet(value)
                if (!srcSetValue.isNullOrEmpty()) return srcSetValue
            } else {
                return value
            }
        }
        return null
    }

    private fun createMedia(
        itemId: String,
        url: String?,
        description: String?,
        tagSnapshot: String,
        width: Int?,
        height: Int?,
    ): BlogMedia? {
        val normalizedUrl = url ?: return null
        if (looksLikeIcon(normalizedUrl, description, tagSnapshot, width, height)) return null
        return BlogMedia(
            id = Md5.md5("$itemId#$normalizedUrl"),
            url = normalizedUrl,
            type = BlogMediaType.IMAGE,
            previewUrl = normalizedUrl,
            remoteUrl = normalizedUrl,
            description = description?.takeIf { it.isNotBlank() },
            blurhash = null,
            meta = null,
        )
    }

    private fun extractAttributeValue(tag: String, attribute: String): String? {
        val regex = Regex(
            pattern = """\b$attribute\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s"'=<>`]+))""",
            options = setOf(RegexOption.IGNORE_CASE),
        )
        val match = regex.find(tag) ?: return null
        val value = match.groups[1]?.value
            ?: match.groups[2]?.value
            ?: match.groups[3]?.value
            ?: return null
        return decodeHtml(value).trim().takeIf { it.isNotEmpty() }
    }

    private fun extractDimension(tag: String, attribute: String): Int? {
        val directValue = extractAttributeValue(tag, attribute)?.toIntOrNull()
        if (directValue != null) return directValue
        val style = extractAttributeValue(tag, "style") ?: return null
        val regex = Regex(
            pattern = """$attribute\s*:\s*(\d+)px""",
            options = setOf(RegexOption.IGNORE_CASE),
        )
        return regex.find(style)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun pickFromSrcSet(srcSet: String): String? {
        return srcSet.split(",")
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { candidate -> candidate.split(srcSetSplitRegex).firstOrNull() }
            .lastOrNull()
    }

    private fun normalizeUrl(rawUrl: String?, articleUrl: String?): String? {
        val url = decodeHtml(rawUrl.orEmpty()).trim()
        if (url.isEmpty()) return null
        if (url.startsWith("data:", ignoreCase = true)) return null
        if (url.startsWith("blob:", ignoreCase = true)) return null
        if (url.startsWith("http://", ignoreCase = true) || url.startsWith(
                "https://",
                ignoreCase = true
            )
        ) {
            return url
        }
        if (url.startsWith("//")) {
            val scheme = articleUrl
                ?.substringBefore("://", "")
                ?.takeIf { it.isNotBlank() }
                ?: "https"
            return "$scheme:$url"
        }
        val baseUrl = articleUrl
            ?.takeIf {
                it.startsWith("http://", ignoreCase = true) || it.startsWith(
                    "https://",
                    ignoreCase = true
                )
            }
            ?: return null
        val origin = baseUrl.origin() ?: return null
        if (url.startsWith("/")) {
            return origin + normalizePath(url)
        }
        val sanitizedBase = baseUrl.substringBefore('#').substringBefore('?')
        val directory = if (sanitizedBase.endsWith("/")) {
            sanitizedBase
        } else {
            sanitizedBase.substringBeforeLast("/", "$origin/")
        }
        val relativePath = directory.substringAfter(origin, "/").trimEnd('/')
        val normalizedPath = normalizePath("$relativePath/$url")
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
                ".." -> if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex)
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

    private fun looksLikeIcon(
        url: String,
        description: String?,
        tagSnapshot: String,
        width: Int?,
        height: Int?,
    ): Boolean {
        if (width != null && height != null && width <= 64 && height <= 64) {
            return true
        }
        val signature = buildString {
            append(url)
            append(' ')
            append(description.orEmpty())
            append(' ')
            append(tagSnapshot)
        }
        if (!iconKeywordRegex.containsMatchIn(signature)) return false
        if (width == null && height == null) return true
        return (width ?: Int.MAX_VALUE) <= 160 && (height ?: Int.MAX_VALUE) <= 160
    }

    private fun decodeHtml(value: String): String {
        return value
            .replace("&amp;", "&")
            .replace("&#38;", "&")
            .replace("&quot;", "\"")
            .replace("&#34;", "\"")
            .replace("&#39;", "'")
            .replace("&apos;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
    }
}
