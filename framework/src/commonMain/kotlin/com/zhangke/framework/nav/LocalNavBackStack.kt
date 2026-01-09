package com.zhangke.framework.nav

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

val LocalNavBackStack: ProvidableCompositionLocal<NavBackStack<NavKey>?> =
    staticCompositionLocalOf { null }
