package com.zhangke.fread.status.ui.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.zhangke.fread.common.utils.MentionTextUtil

class PostStatusTextVisualTransformation(private val highLightColor: Color) : VisualTransformation {

    companion object {

        private const val MARK_START = Int.MIN_VALUE
        private const val MARK_END = Int.MAX_VALUE
    }

    private val Int.isStartMark: Boolean get() = this == MARK_START
    private val Int.isEndMark: Boolean get() = this == MARK_END

    override fun filter(text: AnnotatedString): TransformedText {
        val hashtags = findHashtags(text.text)
        val mentions = MentionTextUtil.findMentionList(text.text)
        val highlightList = hashtags + mentions
        return TransformedText(
            text = buildAnnotatedString {
                append(text)
                highlightList.forEach {
                    addStyle(
                        style = SpanStyle(
                            color = highLightColor,
                        ),
                        start = it.start,
                        end = it.end,
                    )
                }
            },
            offsetMapping = OffsetMapping.Identity,
        )
    }

    private fun findHashtags(text: String): List<TextRange> {
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
