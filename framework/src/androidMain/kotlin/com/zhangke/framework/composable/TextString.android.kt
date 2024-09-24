package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import com.zhangke.framework.utils.appContext

@Composable
actual fun stringResource(resId: Int, vararg formatArgs: Any): String {
    return androidx.compose.ui.res.stringResource(resId, *formatArgs)
}

actual suspend fun TextString.getString(): String {
    return when (this) {
        is TextString.StringText -> string
        is TextString.ResourceText -> appContext.getString(resId, *formatArgs)
        is TextString.ComposeResourceText -> org.jetbrains.compose.resources.getString(res)
    }
}
