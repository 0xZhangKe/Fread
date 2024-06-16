package com.zhangke.fread.status.ui.threads

import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ThreadsStyle(
    val lineWidth: Dp,
    val color: Color,
)

object ThreadsStyleDefaults {

    val width = 1.5.dp

    val color: Color @Composable get() = DividerDefaults.color
}

@Composable
fun defaultThreadsStyle(): ThreadsStyle {
    return ThreadsStyle(
        lineWidth = ThreadsStyleDefaults.width,
        color = ThreadsStyleDefaults.color,
    )
}
