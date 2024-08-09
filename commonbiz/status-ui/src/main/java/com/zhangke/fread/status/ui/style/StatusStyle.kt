package com.zhangke.fread.status.ui.style

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    data class TopLabelStyle(
        val iconSize: Dp,
        val textSize: TextUnit,
    ) {

        companion object {

            fun default(
                iconSize: Dp = 14.dp,
                textSize: TextUnit = 12.sp,
            ): TopLabelStyle = TopLabelStyle(
                iconSize = iconSize,
                textSize = textSize,
            )
        }
    }

    data class ContentStyle(
        val maxLine: Int,
        val titleSize: TextUnit,
        val contentSize: TextUnit,
        val startPadding: Dp,
        val contentToInfoLineSpacing: Dp,
        val textToAttachmentSpacing: Dp,
    ) {

        companion object {

            fun default(
                maxLine: Int = 10,
                titleSize: TextUnit = 16.sp,
                contentSize: TextUnit = 14.sp,
                startPadding: Dp = 0.dp,
                contentToInfoLineSpacing: Dp = 2.dp,
                textToAttachmentSpacing: Dp = 8.dp,
            ) = ContentStyle(
                maxLine = maxLine,
                titleSize = titleSize,
                contentSize = contentSize,
                startPadding = startPadding,
                contentToInfoLineSpacing = contentToInfoLineSpacing,
                textToAttachmentSpacing = textToAttachmentSpacing,
            )
        }
    }

    data class InfoLineStyle(
        val nameSize: TextUnit,
        val avatarSize: Dp,
        val nameToAvatarSpacing: Dp,
        val descStyle: TextStyle,
    ) {

        companion object {

            @Composable
            fun default(
                nameSize: TextUnit = 16.sp,
                avatarSize: Dp = 40.dp,
                nameToAvatarSpacing: Dp = 8.dp,
                descStyle: TextStyle = MaterialTheme.typography.bodySmall
            ): InfoLineStyle {
                return InfoLineStyle(
                    nameSize = nameSize,
                    avatarSize = avatarSize,
                    nameToAvatarSpacing = nameToAvatarSpacing,
                    descStyle = descStyle,
                )
            }
        }
    }

    data class BottomPanelStyle(
        val iconSize: Dp,
        val topPadding: Dp,
        val startPadding: Dp,
    ) {

        companion object {

            fun default(
                iconSize: Dp = 32.dp,
                topPadding: Dp = 4.dp,
                startPadding: Dp = 0.dp,
            ) = BottomPanelStyle(
                iconSize = iconSize,
                topPadding = topPadding,
                startPadding = startPadding,
            )
        }
    }

    data class ThreadsStyle(
        val lineWidth: Dp,
        val color: Color,
    ) {

        companion object {

            @Composable
            fun default(
                lineWidth: Dp = 1.5.dp,
                color: Color = DividerDefaults.color,
            ) = ThreadsStyle(
                lineWidth = lineWidth,
                color = color,
            )
        }
    }

    companion object {

        @Composable
        fun default(
            containerStartPadding: Dp = 16.dp,
            containerTopPadding: Dp = 8.dp,
            containerEndPadding: Dp = 16.dp,
            containerBottomPadding: Dp = 8.dp,
            topLabelStyle: TopLabelStyle = TopLabelStyle.default(),
            infoLineStyle: InfoLineStyle = InfoLineStyle.default(),
            contentStyle: ContentStyle = ContentStyle.default(),
            bottomPanelStyle: BottomPanelStyle = BottomPanelStyle.default(),
            threadsStyle: ThreadsStyle = ThreadsStyle.default(),
        ) = StatusStyle(
            containerStartPadding = containerStartPadding,
            containerTopPadding = containerTopPadding,
            containerEndPadding = containerEndPadding,
            containerBottomPadding = containerBottomPadding,
            topLabelStyle = topLabelStyle,
            infoLineStyle = infoLineStyle,
            contentStyle = contentStyle,
            bottomPanelStyle = bottomPanelStyle,
            threadsStyle = threadsStyle,
        )

    }
}
