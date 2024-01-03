package com.zhangke.utopia.status.ui.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StatusStyle(
    val containerPaddings: PaddingValues,
    val iconStartPadding: Dp,
    val iconEndPadding: Dp,
    val statusInfoStyle: StatusInfoStyle,
    val blogStyle: BlogStyle,
)

object StatusStyleDefaults {

    val startPadding = 16.dp

    val topPadding = 8.dp

    val endPadding = 16.dp

    val bottomPadding = 8.dp

    val iconStartPadding = 8.dp

    val iconEndPadding = 8.dp
}

@Composable
fun defaultStatusStyle() = StatusStyle(
    containerPaddings = PaddingValues(
        start = StatusStyleDefaults.startPadding,
        top = StatusStyleDefaults.topPadding,
        end = StatusStyleDefaults.endPadding,
        bottom = StatusStyleDefaults.bottomPadding,
    ),
    iconStartPadding = StatusStyleDefaults.iconStartPadding,
    iconEndPadding = StatusStyleDefaults.iconEndPadding,
    statusInfoStyle = defaultStatusInfoStyle(),
    blogStyle = defaultBlogStyle(),
)
