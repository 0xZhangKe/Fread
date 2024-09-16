package com.zhangke.framework.composable

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarHostState: ProvidableCompositionLocal<SnackbarHostState?> =
    staticCompositionLocalOf { null }
