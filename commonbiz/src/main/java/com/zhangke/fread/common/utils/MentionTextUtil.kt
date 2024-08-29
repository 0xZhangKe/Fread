package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring

object MentionTextUtil {

    fun findTypingMentionName(text: TextFieldValue): String? {
        if (text.selection.length != 0) return null
        if (text.selection.start <= 0) return null
        val chars = text.text.toCharArray()
        val range = TypingMentionMather().findTypingMentionRange(
            chars = chars,
            startIndex = text.selection.start - 1,
        ) ?: return null
        return text.text.substring(range)
    }
}

class TypingMentionMather {

    companion object {

        private const val STATE_INIT = 0

        /**
         * scanning @AtomZ@m.cmx.im or @AtomX suffix
         */
        private const val STATE_SCANNING_NAME_OR_HOST = STATE_INIT + 1

        /**
         * scanning @AtomZ@m.cmx.im name part
         */
        private const val STATE_SCANNING_NAME = STATE_SCANNING_NAME_OR_HOST + 1
    }

    fun findTypingMentionRange(chars: CharArray, startIndex: Int): TextRange? {
        if (startIndex <= 0 || startIndex >= chars.size) return null
        var state = STATE_INIT
        var index = startIndex
        var firstMentionFlagIndex = -1
        while (index >= 0) {
            when (state) {
                STATE_INIT -> {
                    state = STATE_SCANNING_NAME_OR_HOST
                }

                STATE_SCANNING_NAME_OR_HOST -> {
                    if (chars[index].validateWebFinerSymbol) {
                        index--
                    } else if (chars[index].isMentionFlag) {
                        firstMentionFlagIndex = index
                        index--
                        state = STATE_SCANNING_NAME
                    } else {
                        return null
                    }
                }

                STATE_SCANNING_NAME -> {
                    if (chars[index].validateWebFinerSymbol) {
                        index--
                    } else if (chars[index].isMentionFlag) {
                        if (firstMentionFlagIndex - index == 1) break
                        return null
                    } else {
                        break
                    }
                }
            }
        }
        if (state == STATE_SCANNING_NAME) {
            return TextRange(firstMentionFlagIndex, startIndex + 1)
        }
        return null
    }
}

private val Char.isMentionFlag: Boolean get() = this == '@'

private val validateWebFingerSymbols = arrayOf(
    '-', '.', '_', '+', '%', '!', '=', '~', '*'
)

private val Char.validateWebFinerSymbol: Boolean
    get() {
        return this.isLetterOrDigit() || this in validateWebFingerSymbols
    }
