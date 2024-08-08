package com.zhangke.fread.status.ui.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class StatusStyle(
    val containerStartPadding: Dp,
    val containerTopPadding: Dp,
    val containerEndPadding: Dp,
    val containerBottomPadding: Dp,
    val contentToInfoSpacing: Dp,
    val bottomPanelStartPadding: Dp,
    val iconEndPadding: Dp,
    val bottomPanelTopPadding: Dp,
    val statusInfoStyle: StatusInfoStyle,
    val contentMaxLine: Int = 10,
    val contentSize: StatusContentSize = StatusContentSize.default(),
){

    data class TopLabelStyle(
        val iconSize: Dp,
        val textSize: TextUnit,
        val containerHeight: Dp,
    )

    data class ContentStyle(
        val maxLine: Int,
        val textSize: TextUnit,
        val contentToInfoLineSpacing: Dp,
        val textToAttachmentSpacing: Dp,
    )

    data class InfoLineStyle(
        val avatarSize: Dp,
        val nameSize: TextUnit,
        val datetimeSize: TextUnit,
        val acctSize: TextUnit,
        val visibilityLabelSize: Dp,
        val nameToAcctSpacing: Dp,
    )

    data class BottomPanelStyle(
        val height: Dp,
        val iconSize: Dp,
        val startPadding: Dp,
        val endPadding: Dp,
    )
}

object StatusStyleDefaults {

    val startPadding = 16.dp

    val topPadding = 16.dp

    val endPadding = 16.dp

    val contentToInfoSpacing = 4.dp

    val bottomPadding = 8.dp

    val bottomPanelStartPadding = 8.dp

    val iconEndPadding = 8.dp

    val bottomPanelTopPadding = 4.dp

    const val contentMaxLine = 10
}

@Composable
fun defaultStatusStyle(
    containerStartPadding: Dp = StatusStyleDefaults.startPadding,
    containerTopPadding: Dp = StatusStyleDefaults.topPadding,
    containerEndPadding: Dp = StatusStyleDefaults.endPadding,
    containerBottomPadding: Dp = StatusStyleDefaults.bottomPadding,
    infoToContentSpacing: Dp = StatusStyleDefaults.contentToInfoSpacing,
    contentMaxLine: Int = StatusStyleDefaults.contentMaxLine,
    bottomPanelStartPadding: Dp = StatusStyleDefaults.bottomPanelStartPadding,
    iconEndPadding: Dp = StatusStyleDefaults.iconEndPadding,
    bottomPanelTopPadding: Dp = StatusStyleDefaults.bottomPanelTopPadding,
    statusInfoStyle: StatusInfoStyle = defaultStatusInfoStyle(),
) = StatusStyle(
    containerStartPadding = containerStartPadding,
    containerTopPadding = containerTopPadding,
    containerEndPadding = containerEndPadding,
    containerBottomPadding = containerBottomPadding,
    contentToInfoSpacing = infoToContentSpacing,
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
