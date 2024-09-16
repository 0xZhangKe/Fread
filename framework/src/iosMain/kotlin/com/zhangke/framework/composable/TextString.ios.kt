package com.zhangke.framework.composable

import androidx.compose.runtime.Composable

@Composable
actual fun stringResource(resId: Int, vararg formatArgs: Any): String {
    error("stringResource(resId, formatArgs) Not supported on iOS")
}