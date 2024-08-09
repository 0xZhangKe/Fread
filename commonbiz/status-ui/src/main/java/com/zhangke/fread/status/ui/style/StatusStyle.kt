package com.zhangke.fread.status.ui.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

// 垂直方向的 padding 外面统一加
data class StatusStyle(
    val containerStartPadding: Dp,
    val containerTopPadding: Dp,
    val containerEndPadding: Dp,
    val containerBottomPadding: Dp,
    val topLabelStyle: TopLabelStyle,
    val infoLineStyle: InfoLineStyle,
    val contentStyle: ContentStyle,
    val bottomPanelStyle: BottomPanelStyle,
    val threadsStyle: ThreadsStyle,
) {

    fun contentIndentStyle(): StatusStyle {
        val contentStartPadding = infoLineStyle.avatarSize + infoLineStyle.nameToAvatarSpacing
        return copy(
            contentStyle = contentStyle.copy(startPadding = contentStartPadding),
            bottomPanelStyle = bottomPanelStyle.copy(startPadding = contentStartPadding),
        )
    }

    data class TopLabelStyle(
        val iconSize: Dp,
        val textSize: TextUnit,
    )

    data class ContentStyle(
        val maxLine: Int,
        val titleSize: TextUnit,
        val contentSize: TextUnit,
        val startPadding: Dp,
        val contentToInfoLineSpacing: Dp,
        val textToAttachmentSpacing: Dp,
    )

    data class InfoLineStyle(
        val nameSize: TextUnit,
        val avatarSize: Dp,
        val nameToAvatarSpacing: Dp,
        val descStyle: TextStyle,
    )

    data class BottomPanelStyle(
        val iconSize: Dp,
        val topPadding: Dp,
        val startPadding: Dp,
    )

    data class ThreadsStyle(
        val lineWidth: Dp,
        val color: Color,
    )
}
