package com.zhangke.fread.utils

import androidx.compose.runtime.staticCompositionLocalOf

expect class ActivityHelper {
    fun goHome()
}

internal val LocalActivityHelper = staticCompositionLocalOf<ActivityHelper> {
    error("No ActivityHelper provided")
}
