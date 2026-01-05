package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange

object HashtagTextUtils2 {

    private const val MARK_START = Int.MIN_VALUE
    private const val MARK_END = Int.MAX_VALUE

    private val EXCLUDED_CHARS =
        setOf('\u00AD', '\u2060', '\u200A', '\u200B', '\u200C', '\u200D', '\u20e2')

    private val Int.isStartMark: Boolean get() = this == MARK_START
    private val Int.isEndMark: Boolean get() = this == MARK_END

    /**
     * Returns `true` if this character is a Unicode punctuation character.
     *
     * Equivalent to testing if the char would be matched by the regular expression
     * `\p{P}` or `\p{Punctuation}` (with Unicode enabled).
     *
     * Specifically, a character is a Unicode punctuation character if its [category] is one of
     * [CharCategory.CONNECTOR_PUNCTUATION], [CharCategory.DASH_PUNCTUATION],
     * [CharCategory.START_PUNCTUATION], [CharCategory.END_PUNCTUATION],
     * [CharCategory.INITIAL_QUOTE_PUNCTUATION], [CharCategory.FINAL_QUOTE_PUNCTUATION],
     * and [CharCategory.OTHER_PUNCTUATION].
     */
    private fun Char.isPunctuation(): Boolean {
        return when (this.category) {
            CharCategory.CONNECTOR_PUNCTUATION,
            CharCategory.DASH_PUNCTUATION,
            CharCategory.START_PUNCTUATION,
            CharCategory.END_PUNCTUATION,
            CharCategory.INITIAL_QUOTE_PUNCTUATION,
            CharCategory.FINAL_QUOTE_PUNCTUATION,
            CharCategory.OTHER_PUNCTUATION
                -> true
            else -> false
        }
    }

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
     * Returns `true` if this character is a Unicode symbol.
     *
     * Equivalent to testing if the char would be matched by the regular expression
     * `\p{S}` or `\p{Symbol}` (with Unicode enabled).
     *
     * Specifically, a character is a Unicode symbol if its [category] is one of
     * [CharCategory.MATH_SYMBOL], [CharCategory.CURRENCY_SYMBOL],
     * [CharCategory.MODIFIER_SYMBOL], and [CharCategory.OTHER_SYMBOL].
     */
    private fun Char.isSymbol(): Boolean {
        return when (this.category) {
            CharCategory.MATH_SYMBOL,
            CharCategory.CURRENCY_SYMBOL,
            CharCategory.MODIFIER_SYMBOL,
            CharCategory.OTHER_SYMBOL
                -> true
            else -> false
        }
    }

    fun findHashtags(text: String, allowHashtagInHashtag: Boolean = true): List<TextRange> {
        if (text.isEmpty()) return emptyList()
        val list = mutableListOf<TextRange>()
        val chars = text.toCharArray()
        var index = 0
        var start = MARK_START
        var end = MARK_END
        var lastEnd = MARK_END
        var prevIsSep = true
        var hasAlpha = false
        // "alpha" for purposes of bsky:
        // not digit, not whitespace, not punctuation
        while (index < text.length) {
            val char = chars[index]
            when {
                char == '#' -> {
                    if (prevIsSep) {
                        start = index
                        hasAlpha = false
                        lastEnd = MARK_END
                    } else if (!start.isStartMark) {
                        hasAlpha = false
                    }
                    prevIsSep = false
                }

                char.isWhitespace() -> {
                    if (!start.isStartMark && end.isEndMark) {
                        end = if (hasAlpha) index else lastEnd
                        if (end.isEndMark) {
                            start = MARK_START
                        }
                    }
                    prevIsSep = true
                }

                EXCLUDED_CHARS.contains(char) -> {
                    if (!start.isStartMark && end.isEndMark) {
                        end = if (hasAlpha) index else lastEnd
                        if (end.isEndMark) {
                            start = MARK_START
                        }
                    }
                    prevIsSep = false
                }

                char.isLetter() || char.isMark() || char.isSymbol() -> {
                    prevIsSep = false
                    hasAlpha = true
                    lastEnd = index + 1
                }

                else -> {
                    prevIsSep = false
                    hasAlpha = false
                }
            }
            if (!start.isStartMark && !end.isEndMark) {
                list += TextRange(start = start, end = end)
                start = MARK_START
                end = MARK_END
            }
            index++
        }

        if (index == text.length && !start.isStartMark && end.isEndMark) {
            end = if (hasAlpha) index else lastEnd
            if (!end.isEndMark) {
                list += TextRange(start = start, end = end)
            }
        }
        return list
    }
}
