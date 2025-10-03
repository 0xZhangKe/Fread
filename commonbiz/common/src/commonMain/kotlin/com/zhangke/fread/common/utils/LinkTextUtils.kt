package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange

object LinkTextUtils {

    private val urlRegex = """
        (?:https?://)?(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\.)+[A-Za-z]{2,63}(?!\.[A-Za-z0-9-])(?=$|[:/?#]|[^\w-])(?::\d{2,5})?(?:[/?#][^\s'"]*)?
    """.trimIndent().toRegex()

    fun findLinks(text: String): List<TextRange> {
        if (text.isEmpty()) return emptyList()
        val list = mutableListOf<TextRange>()
        urlRegex.findAll(text)
            .forEach {
                list += TextRange(start = it.range.start, end = it.range.endInclusive + 1)
            }
        return list
    }
}
