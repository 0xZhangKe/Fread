package com.zhangke.fread.status.ui.threads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StatusThread(
    modifier: Modifier,
    style: ThreadsStyle = defaultThreadsStyle(),
) {
    Box(
        modifier = modifier
            .width(style.lineWidth)
            .background(color = style.color, shape = RoundedCornerShape(style.lineWidth / 2)),
    )
}
