package com.zhangke.fread.common.utils

import androidx.compose.runtime.staticCompositionLocalOf

expect class ToastHelper {
    fun showToast(content: String)
}

val LocalToastHelper = staticCompositionLocalOf<ToastHelper> {
    error("No ToastHelper provided")
}
