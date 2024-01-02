package com.zhangke.utopia.status.ui.threads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatusThread(
    modifier: Modifier,
    style: ThreadsStyle = defaultThreadsStyle(),
) {
    Box(
        modifier = modifier
            .padding(vertical = 1.dp)
            .width(style.lineWidth)
            .background(color = style.color, shape = RoundedCornerShape(style.lineWidth / 2)),
    )
}
