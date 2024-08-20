package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

object DeleteTextUtil {

    fun deleteText(text: TextFieldValue): TextFieldValue {
        if (text.selection.length > 0) {
            val startIndex = text.selection.start
            val newText = text.text.remove(text.selection)
            return TextFieldValue(newText, TextRange(startIndex))
        }
        val startIndex = text.selection.start

        return text
    }
}

private fun String.remove(range: TextRange): String {
    if (this.isEmpty()) return this
    if (range.length <= 0) return this
    if (range.start < 0 || range.end > this.length) return this
    val prefix = this.substring(0, range.start)
    val suffix = this.substring(range.end)
    return prefix + suffix
}

// can be space or colon
private const val STATE_SCAN_FIRST_SYMBOL = 0

// must be colon
private const val STATE_SCAN_COLON = 1

// can be anything but blank, end with colon
private const val STATE_SCAN_CODE = 2

// must be space
private const val STATE_SCAN_END_SPACE = 3

// 判断 index 之前是否是 emoji
private fun findBeforeEmoji(text: String, index: Int): TextRange? {
    //“ :1234: ”
    text.isNullOrBlank()
    if (index <= 0 || index > text.length) return null
    var currentIndex = index - 1

    return null
}
