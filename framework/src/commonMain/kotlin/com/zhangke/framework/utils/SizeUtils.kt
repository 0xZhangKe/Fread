package com.zhangke.framework.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints

fun Constraints.asSize(): Size {
    return Size(
        width = maxWidth.toFloat(),
        height = maxHeight.toFloat(),
    )
}
