package com.zhangke.fread.status.ui.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StatusStyle(
    val containerPaddings: PaddingValues,
    val bottomPanelStartPadding: Dp,
    val iconEndPadding: Dp,
    val bottomPanelTopPadding: Dp,
    val statusInfoStyle: StatusInfoStyle,
    val contentMaxLine: Int = 10,
    val contentSize: StatusContentSize = StatusContentSize.default(),
)

object StatusStyleDefaults {

    val startPadding = 16.dp

    val topPadding = 8.dp

    val endPadding = 16.dp

    val bottomPadding = 4.dp

    val bottomPanelStartPadding = 8.dp

    val iconEndPadding = 8.dp

    val bottomPanelTopPadding = 4.dp

    const val contentMaxLine = 10
}

@Composable
fun defaultStatusStyle(
    containerPaddings: PaddingValues = PaddingValues(
        start = StatusStyleDefaults.startPadding,
        top = StatusStyleDefaults.topPadding,
        end = StatusStyleDefaults.endPadding,
        bottom = StatusStyleDefaults.bottomPadding,
    ),
    contentMaxLine: Int = StatusStyleDefaults.contentMaxLine,
    bottomPanelStartPadding: Dp = StatusStyleDefaults.bottomPanelStartPadding,
    iconEndPadding: Dp = StatusStyleDefaults.iconEndPadding,
    bottomPanelTopPadding: Dp = StatusStyleDefaults.bottomPanelTopPadding,
    statusInfoStyle: StatusInfoStyle = defaultStatusInfoStyle(),
) = StatusStyle(
    containerPaddings = containerPaddings,
    bottomPanelStartPadding = bottomPanelStartPadding,
    iconEndPadding = iconEndPadding,
    statusInfoStyle = statusInfoStyle,
    bottomPanelTopPadding = bottomPanelTopPadding,
    contentMaxLine = contentMaxLine,
)

data class StatusContentSize(
    val topLabelSize: TextUnit,
    val userNameSize: TextUnit,
    val infoSize: TextUnit,
    val blogTitleSize: TextUnit,
    val bogContentSize: TextUnit,
) {

    companion object {

        fun default(
            topLabelSize: TextUnit = 12.sp,
            userNameSize: TextUnit = 16.sp,
            infoSize: TextUnit = 12.sp,
            blogTitleSize: TextUnit = 16.sp,
            bogContentSize: TextUnit = 14.sp,
        ) = StatusContentSize(
            topLabelSize = topLabelSize,
            userNameSize = userNameSize,
            infoSize = infoSize,
            blogTitleSize = blogTitleSize,
            bogContentSize = bogContentSize,
        )
    }
}
