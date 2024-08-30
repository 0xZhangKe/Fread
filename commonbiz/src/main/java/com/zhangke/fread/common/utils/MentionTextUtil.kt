package com.zhangke.fread.common.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring

object MentionTextUtil {

    fun findTypingMentionName(text: TextFieldValue): String? {
        val range = findTypingMentionRange(text) ?: return null
        return text.text.substring(range)
    }

    fun insertMention(text: TextFieldValue, insertText: String): TextFieldValue {
        if (insertText.isEmpty()) return text
        val fixedInsertText = "@$insertText"
        val range = findTypingMentionRange(text)
        if (range == null) {
            val newText = "${text.text} $fixedInsertText "
            return TextFieldValue(text = newText, TextRange(newText.length))
        } else {
            val finalInsertText = " $fixedInsertText "
            val newText = text.text.replaceRange(
                startIndex = range.start,
                endIndex = range.end,
                replacement = finalInsertText,
            )
            return TextFieldValue(text = newText, TextRange(range.start + finalInsertText.length))
        }
    }

//    fun findMentionList(text: String): List<TextRange> {
//        if (text.isEmpty()) return emptyList()
//        val list = mutableListOf<TextRange>()
//        val matcher = TypingMentionMather()
//        var index = text.length - 1
//        val chars = text.toCharArray()
//        while (index > 0) {
//            val range = matcher.findTypingMentionRange(chars, index)
//            if (range)
//        }
//
//        return list
//    }

    private fun findTypingMentionRange(text: TextFieldValue): TextRange? {
        if (text.selection.length != 0) return null
        if (text.selection.start <= 0) return null
        val chars = text.text.toCharArray()
        return TypingMentionMather().findTypingMentionRange(
            chars = chars,
            startIndex = text.selection.start - 1,
        )
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
                        return TextRange(index, startIndex + 1)
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

//    fun findMentionRanges(chars: CharArray): List<TextRange> {
//        if (chars.isEmpty()) return emptyList()
//        var state = STATE_INIT
//        var index = startIndex
//        var firstMentionFlagIndex = -1
//        while (index >= 0) {
//            when (state) {
//                STATE_INIT -> {
//                    state = STATE_SCANNING_NAME_OR_HOST
//                }
//
//                STATE_SCANNING_NAME_OR_HOST -> {
//                    if (chars[index].validateWebFinerSymbol) {
//                        index--
//                    } else if (chars[index].isMentionFlag) {
//                        firstMentionFlagIndex = index
//                        index--
//                        state = STATE_SCANNING_NAME
//                    } else {
//                        return null
//                    }
//                }
//
//                STATE_SCANNING_NAME -> {
//                    if (chars[index].validateWebFinerSymbol) {
//                        index--
//                    } else if (chars[index].isMentionFlag) {
//                        if (firstMentionFlagIndex - index == 1) break
//                        return TextRange(index, startIndex + 1)
//                    } else {
//                        break
//                    }
//                }
//            }
//        }
//        if (state == STATE_SCANNING_NAME) {
//            return TextRange(firstMentionFlagIndex, startIndex + 1)
//        }
//        return null
//    }
}

private val Char.isMentionFlag: Boolean get() = this == '@'

private val validateWebFingerSymbols = arrayOf(
    '-', '.', '_', '+', '%', '!', '=', '~', '*'
)

private val Char.validateWebFinerSymbol: Boolean
    get() {
        return this.isLetterOrDigit() || this in validateWebFingerSymbols
    }
