package com.zhangke.framework.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        toPx()
    }
}

fun Dp.dpToPx(density: Density): Float {
    return with(density) {
        toPx()
    }
}

fun Int.pxToDp(density: Density): Dp {
    val pxValue = this
    return with(density) { pxValue.toDp() }
}

fun Float.pxToDp(density: Density): Dp {
    val pxValue = this
    return with(density) { pxValue.toDp() }
}
