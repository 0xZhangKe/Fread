package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.framework.unknown_error
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


sealed class TextString {
    class ResourceText(val resId: Int, val formatArgs: Array<Any>) : TextString()
    class ComposeResourceText(val res: StringResource, val formatArgs: Array<Any>) : TextString()
    class StringText(val string: String) : TextString() {
        override fun toString(): String {
            return string
        }
    }
}

fun textOf(stringResId: Int, vararg formatArgs: Any): TextString {
    return TextString.ResourceText(stringResId, arrayOf(*formatArgs))
}

fun textOf(string: String): TextString {
    return TextString.StringText(string)
}

fun textOf(stringResource: StringResource, vararg formatArgs: Any): TextString {
    return TextString.ComposeResourceText(stringResource, arrayOf(*formatArgs))
}

@Composable
fun textString(text: TextString): String {
    return when (text) {
        is TextString.StringText -> text.string
        is TextString.ComposeResourceText -> stringResource(text.res, *text.formatArgs)
        is TextString.ResourceText -> stringResource(text.resId, *text.formatArgs)
    }
}

@Composable
expect fun stringResource(resId: Int, vararg formatArgs: Any): String

@Composable
fun TextString?.isNullOrEmpty(): Boolean {
    return this == null || textString(text = this).isEmpty()
}

fun Throwable.toTextStringOrNull(): TextString? {
    val errorMessage = this.message
    if (errorMessage.isNullOrEmpty()) return null
    return textOf(errorMessage)
}

suspend fun MutableSharedFlow<TextString>.emitTextMessageFromThrowable(t: Throwable) {
    val message = t.toTextStringOrNull()
        ?: textOf(getString(com.zhangke.fread.framework.Res.string.unknown_error))
    this.emit(message)
}

expect suspend fun TextString.getString(): String
