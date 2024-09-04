package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange

object HashtagTextUtils {

    private const val MARK_START = Int.MIN_VALUE
    private const val MARK_END = Int.MAX_VALUE

    private val Int.isStartMark: Boolean get() = this == MARK_START
    private val Int.isEndMark: Boolean get() = this == MARK_END

    fun findHashtags(text: String): List<TextRange> {
        if (text.isEmpty()) return emptyList()
        val list = mutableListOf<TextRange>()
        val chars = text.toCharArray()
        var index = 0
        var start = MARK_START
        var end = MARK_END
        while (index < text.length) {
            val char = chars[index]
            when {
                char == '#' -> {
                    if (start.isStartMark) {
                        start = index
                    } else if (!end.isEndMark) {
                        // abc#cde#
                        end = index
                    }
                }

                char.isWhitespace() -> {
                    if (!start.isStartMark && end.isEndMark) {
                        end = index
                    }
                }
            }
            if (!start.isStartMark && !end.isEndMark) {
                list += TextRange(start = start, end = end + 1)
                start = MARK_START
                end = MARK_END
            }
            index++
        }

        if (index == text.length && !start.isStartMark && end.isEndMark) {
            list += TextRange(start = start, end = index)
        }
        return list
    }
}
