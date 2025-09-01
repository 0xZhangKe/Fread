package com.zhangke.framework.utils

import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

val TextFieldDefaults.transparentIndicatorColors: TextFieldColors
    @Composable get() = colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
    )

val TextFieldDefaults.transparentIndicatorAndContainerColors: TextFieldColors
    @Composable get() = colors(
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
    )
