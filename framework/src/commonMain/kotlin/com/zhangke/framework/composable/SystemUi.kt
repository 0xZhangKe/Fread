package com.zhangke.framework.composable

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.utils.pxToDp

@Composable
fun getNavigationBarHeight(insets: WindowInsets = WindowInsets.navigationBars): Dp {
    val density = LocalDensity.current
    return insets.getTop(density).pxToDp(density)
}
