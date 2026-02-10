package com.zhangke.framework.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) {
        toPx()
    }
}

@Composable
fun Dp.roundToPx(): Int {
    return with(LocalDensity.current) {
        toPx().roundToInt()
    }
}

fun Dp.dpToPx(density: Density): Float {
    return with(density) {
        toPx()
    }
}

fun Int.dpToPx(density: Density): Float {
    return Dp(this.toFloat()).dpToPx(density)
}

fun Int.pxToDp(density: Density): Dp {
    val pxValue = this
    return with(density) { pxValue.toDp() }
}

fun Float.pxToDp(density: Density): Dp {
    val pxValue = this
    return with(density) { pxValue.toDp() }
}
