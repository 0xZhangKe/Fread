package com.zhangke.framework.utils

object ExtractUrlFromTextUtils {

    private val urlRegex = """
        (?:https?://)?(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\.)+[A-Za-z]{2,63}(?!\.[A-Za-z0-9-])(?=$|[:/?#]|[^\w-])(?::\d{2,5})?(?:[/?#][^\s'"]*)?
    """.trimIndent().toRegex()

    private val trailingPunctuation = setOf('.', ',', '!', '?', ':', ';', '"', '\'', '”', '’')
    private val closingToOpeningBracket = mapOf(')' to '(', ']' to '[', '}' to '{')

    fun extract(text: String): List<String> {
        if (text.isEmpty()) return emptyList()
        return urlRegex.findAll(text)
            .mapNotNull { sanitize(it.value) }
            .toList()
    }

    private fun sanitize(candidate: String): String? {
        var url = candidate
        while (url.isNotEmpty() && shouldTrimLastChar(url)) {
            url = url.dropLast(1)
        }
        return url.takeIf { it.isNotEmpty() && urlRegex.matchEntire(it) != null }
    }

    private fun shouldTrimLastChar(url: String): Boolean {
        val lastChar = url.last()
        if (lastChar in trailingPunctuation) return true
        val openingBracket = closingToOpeningBracket[lastChar] ?: return false
        return url.count { it == lastChar } > url.count { it == openingBracket }
    }
}
