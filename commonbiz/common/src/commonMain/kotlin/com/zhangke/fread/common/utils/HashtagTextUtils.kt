package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange

object HashtagTextUtils {

    private const val MARK_START = Int.MIN_VALUE
    private const val MARK_END = Int.MAX_VALUE

    private val Int.isStartMark: Boolean get() = this == MARK_START
    private val Int.isEndMark: Boolean get() = this == MARK_END

    /**
     * Returns `true` if this character is a Unicode mark.
     *
     * Equivalent to testing if the char would be matched by the regular expression
     * `\p{M}` or `\p{Mark}` (with Unicode enabled).
     *
     * Specifically, a character is a Unicode mark if its [category] is one of
     * [CharCategory.NON_SPACING_MARK], [CharCategory.COMBINING_SPACING_MARK], and
     * [CharCategory.ENCLOSING_MARK].
     */
    private fun Char.isMark(): Boolean {
        return when (this.category) {
            CharCategory.NON_SPACING_MARK,
            CharCategory.COMBINING_SPACING_MARK,
            CharCategory.ENCLOSING_MARK
                -> true
            else -> false
        }
    }

    /**
     * Returns `true` if this character is a Unicode number.
     *
     * Equivalent to testing if the char would be matched by the regular expression
     * `\p{N}` or `\p{Number}` (with Unicode enabled).
     *
     * Specifically, a character is a Unicode number if its [category] is one of
     * [CharCategory.DECIMAL_DIGIT_NUMBER], [CharCategory.LETTER_NUMBER], and
     * [CharCategory.OTHER_NUMBER].
     */
    private fun Char.isNumber(): Boolean {
        return when (this.category) {
            CharCategory.DECIMAL_DIGIT_NUMBER,
            CharCategory.LETTER_NUMBER,
            CharCategory.OTHER_NUMBER
                -> true
            else -> false
        }
    }


    fun findHashtags(text: String): List<TextRange> {
        if (text.isEmpty()) return emptyList()
        val list = mutableListOf<TextRange>()
        val chars = text.toCharArray()
        var index = 0
        var start = MARK_START
        var end = MARK_END
        var prevIsSep = true
        var hasAlpha = false
        while (index < text.length) {
            val char = chars[index]
            when {
                char == '#' -> {
                    if (prevIsSep) {
                        start = index
                        hasAlpha = false
                    } else if (!start.isStartMark) {
                        end = index
                    }
                    prevIsSep = true
                }

                char.isWhitespace() -> {
                    if (!start.isStartMark && end.isEndMark) {
                        end = index
                    }
                    prevIsSep = true
                }

                char.isLetter() || char.isMark() -> {
                    prevIsSep = false
                    hasAlpha = true
                }

                char.isNumber() || char.category == CharCategory.CONNECTOR_PUNCTUATION -> {
                    prevIsSep = false
                }

                char != '\u00b7' && char != '\u200c' -> {
                    if (!start.isStartMark && end.isEndMark) {
                        end = index
                    }

                    prevIsSep = (char != '/' && char != ')')
                }
            }
            if (!start.isStartMark && !end.isEndMark) {
                if (hasAlpha) {
                    list += TextRange(start = start, end = end)
                }
                start = MARK_START
                end = MARK_END
            }
            index++
        }

        if (index == text.length && !start.isStartMark && end.isEndMark && hasAlpha) {
            list += TextRange(start = start, end = index)
        }
        return list
    }
}