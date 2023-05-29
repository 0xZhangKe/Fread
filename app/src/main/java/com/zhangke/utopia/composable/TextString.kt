package com.zhangke.utopia.composable

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class TextString {

    class ResourceText(@StringRes val stringResId: Int, val formatArgs: Array<Any>) : TextString()

    class StringText(val string: String) : TextString() {

        override fun toString(): String {
            return string
        }
    }
}

fun textOf(@StringRes stringResId: Int, vararg formatArgs: Any): TextString {
    return TextString.ResourceText(stringResId, arrayOf(*formatArgs))
}

fun textOf(string: String): TextString {
    return TextString.StringText(string)
}

@Composable
fun textString(text: TextString): String {
    return when (text) {
        is TextString.ResourceText -> stringResource(id = text.stringResId, text.formatArgs)
        is TextString.StringText -> text.string
    }
}

fun TextString.getString(context: Context): String {
    return when (this) {
        is TextString.ResourceText -> context.getString(stringResId, formatArgs)
        is TextString.StringText -> string
    }
}

@Composable
fun TextString?.isNullOrEmpty(): Boolean {
    return this == null || textString(text = this).isEmpty()
}