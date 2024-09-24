package com.zhangke.framework.composable

import androidx.compose.runtime.Composable

@Composable
actual fun stringResource(resId: Int, vararg formatArgs: Any): String {
    error("stringResource(resId, formatArgs) Not supported on iOS")
}

actual suspend fun TextString.getString(): String {
    return when (this) {
        is TextString.StringText -> string
        is TextString.ComposeResourceText -> org.jetbrains.compose.resources.getString(res)
        is TextString.ResourceText -> error("ResourceText Not supported on iOS")
    }
}