package com.zhangke.utopia.composable

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class Text {

    class ResourceText(@StringRes val stringResId: Int, val formatArgs: Array<Any>) : Text() {

        override fun isEmpty(): Boolean {
            return false
        }
    }

    class StringText(val string: String) : Text() {

        override fun isEmpty(): Boolean {
            return string.isEmpty()
        }
    }

    abstract fun isEmpty(): Boolean
}

fun Text?.isNullOrEmpty(): Boolean{
    return this == null || isEmpty()
}

fun textOf(@StringRes stringResId: Int, vararg formatArgs: Any): Text {
    return Text.ResourceText(stringResId, arrayOf(*formatArgs))
}

fun textOf(string: String): Text {
    return Text.StringText(string)
}

@Composable
fun textString(text: Text): String {
    return when (text) {
        is Text.ResourceText -> stringResource(id = text.stringResId, text.formatArgs)
        is Text.StringText -> text.string
    }
}