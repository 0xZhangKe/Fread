package com.zhangke.framework.voyager

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator

val LocalGlobalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("LocalGlobalNavigator not initialized") }

