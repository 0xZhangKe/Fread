package com.zhangke.fread.status.ui.style

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

    val topPadding = 10.dp

    val endPadding = 16.dp

    val bottomPadding = 14.dp

    val iconStartPadding = 8.dp

    val iconEndPadding = 8.dp
}

@Composable
fun defaultStatusStyle(
    containerPaddings: PaddingValues = PaddingValues(
        start = StatusStyleDefaults.startPadding,
        top = StatusStyleDefaults.topPadding,
        end = StatusStyleDefaults.endPadding,
        bottom = StatusStyleDefaults.bottomPadding,
    ),
    iconStartPadding: Dp = StatusStyleDefaults.iconStartPadding,
    iconEndPadding: Dp = StatusStyleDefaults.iconEndPadding,
    statusInfoStyle: StatusInfoStyle = defaultStatusInfoStyle(),
    blogStyle: BlogStyle = defaultBlogStyle(),
) = StatusStyle(
    containerPaddings = containerPaddings,
    iconStartPadding = iconStartPadding,
    iconEndPadding = iconEndPadding,
    statusInfoStyle = statusInfoStyle,
    blogStyle = blogStyle,
)
