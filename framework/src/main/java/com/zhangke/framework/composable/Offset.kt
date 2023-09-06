package com.zhangke.framework.composable

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import com.zhangke.framework.utils.pxToDp

fun Modifier.offset(offset: Offset): Modifier = composed {
    val density = LocalDensity.current
    offset(x = offset.x.pxToDp(density), y = offset.y.pxToDp(density))
}

val Offset.isZero: Boolean get() = x == 0F && y == 0F
