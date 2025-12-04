package com.zhangke.fread.common.deeplink

import com.eygraber.uri.Uri

object ExternalInputParser {

    /**
     * Parsing text for external input, such as from Chrome.
     * “Copied Text”
     * http://example.com/#:~:text=Copied%20Text
     */
    fun parseExternalText(text: String): String {
        if (text.isEmpty() || text.isBlank()) return text
        if (!text.contains("http")) return text.trim()
        if (!text.startsWith('"')) return text.trim()
        val endIndex = text.indexOf('"', 1)
        if (endIndex == -1) return text.trim()
        val extractedText = text.substring(1, endIndex).trim()
        if (endIndex == text.lastIndex) return extractedText
        val urlText = text.substring(endIndex + 1, text.length).trim()
        val url = Uri.parseOrNull(urlText) ?: return text.trim()
        if (url.fragment?.contains(extractedText) == true) {
            return extractedText
        }
        return text.trim()
    }
}
