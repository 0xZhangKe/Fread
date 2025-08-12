package com.zhangke.fread.status.ui.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
    val infolineToTopLabelPadding: Dp,
    val infoLineStyle: InfoLineStyle,
    val contentStyle: ContentStyle,
    val bottomPanelStyle: BottomPanelStyle,
    val threadsStyle: ThreadsStyle,
    val cardStyle: CardStyle,
    val bottomLabelStyle: BottomLabelStyle,
) {

    val secondaryFontColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

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
        /**
         * 内容部分竖向的间距，不包含顶部和底部，只包含info line to content
         * content to bottom panel 等。
         */
        val contentVerticalSpacing: Dp,
    )

    data class InfoLineStyle(
        val nameSize: TextUnit,
        val avatarSize: Dp,
        val nameToAvatarSpacing: Dp,
        val descStyle: TextStyle,
    )

    data class BottomPanelStyle(
        val iconSize: Dp,
        val startPadding: Dp,
    )

    data class ThreadsStyle(
        val lineWidth: Dp,
        val color: Color,
    )

    data class CardStyle(
        val titleStyle: TextStyle,
        val descStyle: TextStyle,
        val imageBottomPadding: Dp,
        val contentVerticalPadding: Dp,
    )

    // for bottom label, like edited time, post time, application
    // liked count, reblog count, etc.
    data class BottomLabelStyle(
        val textStyle: TextStyle,
    )
}
