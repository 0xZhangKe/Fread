package com.zhangke.framework.composable

import android.content.Context
import androidx.compose.runtime.Composable

@Composable
actual fun stringResource(resId: Int, vararg formatArgs: Any): String {
    return androidx.compose.ui.res.stringResource(resId, *formatArgs)
}

fun TextString.getString(context: Context): String {
    return when (this) {
        is TextString.StringText -> string
        is TextString.ResourceText -> context.getString(resId, *formatArgs)
        is TextString.ComposeResourceText -> error("TextString.getString not support ComposeResourceText")
    }
}
