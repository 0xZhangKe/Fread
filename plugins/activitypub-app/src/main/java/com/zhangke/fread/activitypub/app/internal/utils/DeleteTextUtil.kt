package com.zhangke.fread.activitypub.app.internal.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

object DeleteTextUtil {

    fun deleteText(text: TextFieldValue): TextFieldValue {
        if (text.text.isEmpty()) return text
        val startIndex = text.selection.start
        if (text.selection.length > 0) {
            val newText = text.text.remove(text.selection)
            return TextFieldValue(newText, TextRange(startIndex))
        }
        val range = findBeforeEmoji(text.text, text.selection.start)?.toTextRange()
        if (range != null) {
            val newText = text.text.remove(range)
            return TextFieldValue(newText, TextRange(range.start))
        }
        if (startIndex > 0) {
            val removedRange = TextRange(startIndex - 1, startIndex)
            return TextFieldValue(text.text.remove(removedRange), TextRange(startIndex - 1))
        }
        return text
    }

    private fun IntRange.toTextRange(): TextRange {
        return TextRange(start, endInclusive)
    }

    private fun String.remove(range: TextRange): String {
        if (this.isEmpty()) return this
        if (range.length < 0) return this
        if (range.start < 0 || range.end > this.length) return this
        val prefix = this.substring(0, range.start)
        val suffix = this.substring(range.end)
        return prefix + suffix
    }

    // must be space
    private const val STATE_INIT = 0

    // must be colon
    private const val STATE_SCANNING_START_COLON = 1

    // can be anything but blank
    private const val STATE_SCANNING_CODE = 2

    // must be colon
    private const val STATE_SCANNING_END_COLON = 3

    // must be space
    private const val STATE_SCANNING_END_SPACE = 4

    // 判断 index 之前是否是 emoji
    internal fun findBeforeEmoji(text: String, index: Int): IntRange? {
        //“ :1234_iu01: ”
        if (index <= 0 || index > text.length) return null
        var currentIndex = index - 1
        var state = STATE_INIT
        val charArray = text.toCharArray()
        while (currentIndex >= 0) {
            val char = charArray[currentIndex]
            when (state) {
                STATE_INIT -> {
                    if (char.isSpace) {
                        state = STATE_SCANNING_START_COLON
                        currentIndex--
                    } else {
                        return null
                    }
                }

                STATE_SCANNING_START_COLON -> {
                    if (char.isColon) {
                        state = STATE_SCANNING_CODE
                        currentIndex--
                    } else {
                        return null
                    }
                }

                STATE_SCANNING_CODE -> {
                    if (char.validateCode) {
                        currentIndex--
                    } else if (char.isColon) {
                        state = STATE_SCANNING_END_COLON
                    } else {
                        return null
                    }
                }

                STATE_SCANNING_END_COLON -> {
                    if (char.isColon) {
                        state = STATE_SCANNING_END_SPACE
                        currentIndex--
                    } else {
                        return null
                    }
                }

                STATE_SCANNING_END_SPACE -> {
                    return if (char.isSpace) {
                        IntRange(currentIndex, index)
                    } else {
                        null
                    }
                }
            }
        }
        return null
    }

    private val Char.isSpace: Boolean get() = this == ' '
    private val Char.isColon: Boolean get() = this == ':'
    private val Char.validateCode: Boolean get() = this.isLetterOrDigit() || this == '_'

}
