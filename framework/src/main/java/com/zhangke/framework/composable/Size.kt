package com.zhangke.framework.composable

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.zhangke.framework.utils.pxToDp

fun Modifier.size(size: IntSize): Modifier = composed {
    val density = LocalDensity.current
    size(width = size.width.pxToDp(density), height = size.height.pxToDp(density))
}
