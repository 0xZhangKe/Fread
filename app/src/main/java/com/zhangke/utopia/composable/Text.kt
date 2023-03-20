package com.zhangke.utopia.composable

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class Text {

    class ResourceText(@StringRes val stringResId: Int) : Text()

    class StringText(val string: String) : Text()
}

fun textOf(@StringRes stringResId: Int): Text {
    return Text.ResourceText(stringResId)
}

fun textOf(string: String): Text {
    return Text.StringText(string)
}

@Composable
fun textString(text: Text): String {
    return when (text) {
        is Text.ResourceText -> stringResource(id = text.stringResId)
        is Text.StringText -> text.string
    }
}