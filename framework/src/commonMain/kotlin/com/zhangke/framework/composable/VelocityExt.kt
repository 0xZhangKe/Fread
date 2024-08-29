package com.zhangke.framework.composable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Velocity

fun Velocity.toOffset() = Offset(x = x, y = y)