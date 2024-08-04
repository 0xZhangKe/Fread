package com.zhangke.fread.status.ui.style

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StatusStyle(
    val containerPaddings: PaddingValues,
    val iconStartPadding: Dp,
    val iconEndPadding: Dp,
    val statusInfoStyle: StatusInfoStyle,
    val contentMaxLine: Int = 10,
    val contentSize: StatusContentSize = StatusContentSize.default(),
)

object StatusStyleDefaults {

    val startPadding = 16.dp

    val topPadding = 10.dp

    val endPadding = 16.dp

    val bottomPadding = 14.dp

    val iconStartPadding = 8.dp

    val iconEndPadding = 8.dp

    val contentMaxLine = 10
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
    iconStartPadding: Dp = StatusStyleDefaults.iconStartPadding,
    iconEndPadding: Dp = StatusStyleDefaults.iconEndPadding,
    statusInfoStyle: StatusInfoStyle = defaultStatusInfoStyle(),
) = StatusStyle(
    containerPaddings = containerPaddings,
    iconStartPadding = iconStartPadding,
    iconEndPadding = iconEndPadding,
    statusInfoStyle = statusInfoStyle,
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
