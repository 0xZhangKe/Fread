package com.zhangke.framework.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

object TextFieldUtils {

    fun insertText(value: TextFieldValue, insertText: String): TextFieldValue {
        val selection = value.selection
        val oldText = value.text
        if (oldText.isEmpty()) {
            return TextFieldValue(insertText, TextRange(insertText.length))
        }
        val selectedCount = (selection.end - selection.start).coerceAtLeast(0)
        if (selectedCount == 0) {
            val charList = oldText.toMutableList()
            val newTextList = insertText.toList()
            charList.addAll(selection.start, newTextList)
            val newText = charList.joinToString("")
            return TextFieldValue(newText, TextRange(selection.start + newTextList.size))
        }
        val charList = oldText.toMutableList()
        repeat(selectedCount) {
            charList.removeAt(selection.start)
        }
        val newTextField = TextFieldValue(charList.joinToString(""), TextRange(selection.start))
        return insertText(newTextField, insertText)
    }
}
