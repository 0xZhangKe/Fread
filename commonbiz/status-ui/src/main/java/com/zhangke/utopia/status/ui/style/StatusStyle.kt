package com.zhangke.utopia.status.ui.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StatusStyle(
    val startPadding: Dp,
    val topPadding: Dp,
    val endPadding: Dp,
    val bottomPadding: Dp,
    val statusInfoStyle: StatusInfoStyle,
    val blogStyle: BlogStyle,
)

object StatusStyleDefaults {

    val startPadding = 16.dp

    val topPadding = 8.dp

    val endPadding = 16.dp

    val bottomPadding = 8.dp

}

@Composable
fun defaultStatusStyle() = StatusStyle(
    startPadding = StatusStyleDefaults.startPadding,
    topPadding = StatusStyleDefaults.topPadding,
    endPadding = StatusStyleDefaults.endPadding,
    bottomPadding = StatusStyleDefaults.bottomPadding,
    statusInfoStyle = defaultStatusInfoStyle(),
    blogStyle = defaultBlogStyle(),
)
