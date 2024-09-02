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

    fun findMentionList(text: String): List<TextRange> {
        if (text.isEmpty()) return emptyList()
        return TextMentionMatcher().findMentionRanges(text.toCharArray())
    }

    private fun findTypingMentionRange(text: TextFieldValue): TextRange? {
        if (text.selection.length != 0) return null
        if (text.selection.start <= 0) return null
        val chars = text.text.toCharArray()
        return TypingMentionMatcher().findTypingMentionRange(
            chars = chars,
            startIndex = text.selection.start - 1,
        )
    }
}

class TypingMentionMatcher {

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
}

class TextMentionMatcher {

    companion object {

        private const val STATE_SCANNING_FIRST_MENTION_FLAG = 0
        private const val STATE_SCANNING_NAME = STATE_SCANNING_FIRST_MENTION_FLAG + 1
        private const val STATE_SCANNING_HOST = STATE_SCANNING_NAME + 1
    }

    fun findMentionRanges(chars: CharArray): List<TextRange> {
        if (chars.isEmpty()) return emptyList()
        val list = mutableListOf<TextRange>()
        var state = STATE_SCANNING_FIRST_MENTION_FLAG
        var index = 0
        var nameMentionFlagIndex = -1
        while (index < chars.size) {
            val char = chars[index]
            when (state) {
                STATE_SCANNING_FIRST_MENTION_FLAG -> {
                    if (char.isMentionFlag) {
                        nameMentionFlagIndex = index
                        state = STATE_SCANNING_NAME
                    }
                    index++
                }

                STATE_SCANNING_NAME -> {
                    if (char.isMentionFlag) {
                        state = STATE_SCANNING_HOST
                    } else if (!char.validateWebFinerSymbol) {
                        list += TextRange(nameMentionFlagIndex, index)
                        nameMentionFlagIndex = -1
                        state = STATE_SCANNING_FIRST_MENTION_FLAG
                    }
                    index++
                }

                STATE_SCANNING_HOST -> {
                    if (!char.validateWebFinerSymbol) {
                        if (index - nameMentionFlagIndex > 1) {
                            list += TextRange(nameMentionFlagIndex, index)
                        }
                        state = STATE_SCANNING_FIRST_MENTION_FLAG
                    }
                    index++
                }
            }
        }

        if (state == STATE_SCANNING_HOST && index - nameMentionFlagIndex > 1) {
            list += TextRange(nameMentionFlagIndex, index)
        }
        if (state == STATE_SCANNING_NAME){
            list += TextRange(nameMentionFlagIndex, index)
        }
        return list
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
