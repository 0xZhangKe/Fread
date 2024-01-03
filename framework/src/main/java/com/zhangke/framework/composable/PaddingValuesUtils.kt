package com.zhangke.framework.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalLayoutDirection

fun Modifier.startPadding(paddings: PaddingValues): Modifier = composed {
    val dir = LocalLayoutDirection.current
    this.padding(start = paddings.calculateStartPadding(dir))
}

fun Modifier.endPadding(paddings: PaddingValues): Modifier = composed {
    val dir = LocalLayoutDirection.current
    this.padding(end = paddings.calculateEndPadding(dir))
}

fun Modifier.horizontalPadding(paddings: PaddingValues): Modifier = composed {
    val dir = LocalLayoutDirection.current
    this.padding(
        start = paddings.calculateStartPadding(dir),
        end = paddings.calculateEndPadding(dir),
    )
}
