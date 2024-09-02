package com.zhangke.framework.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

object HighlightTextBuildUtil {

    private const val HIGHLIGHT_START_SYMBOL = "[["
    private const val HIGHLIGHT_END_SYMBOL = "]]"

    /**
     * some text[[high light text]]end text
     */
    fun buildHighlightText(
        text: String,
        fontWeight: FontWeight? = null,
        highLightColor: Color? = null,
        highLightSize: TextUnit? = null,
    ): AnnotatedString {
        return buildAnnotatedString {
            var leftText = text
            while (leftText.isNotEmpty()) {
                val (prefix, highlight, suffix) = findFirstHighlightRange(leftText)
                if (prefix.isNotEmpty()) {
                    append(prefix)
                }
                if (highlight.isNotEmpty()) {
                    withStyle(
                        style = SpanStyle(
                            color = highLightColor ?: Color.Unspecified,
                            fontSize = highLightSize ?: TextUnit.Unspecified,
                            fontWeight = fontWeight,
                        ),
                    ) {
                        append(highlight)
                    }
                }
                leftText = suffix
            }
        }
    }

    private fun findFirstHighlightRange(text: String): Triple<String, String, String> {
        val start = text.indexOf(HIGHLIGHT_START_SYMBOL)
        if (start == -1) return Triple(text, "", "")
        val end = text.indexOf(HIGHLIGHT_END_SYMBOL, start)
        if (end == -1) {
            return Triple(text, "", "")
        }
        val prefix = text.substring(0, start)
        val highlight = text.substring(start + HIGHLIGHT_START_SYMBOL.length, end)
        val suffix = text.substring(end + HIGHLIGHT_END_SYMBOL.length)
        return Triple(prefix, highlight, suffix)
    }
}
