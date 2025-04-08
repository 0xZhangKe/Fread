package com.zhangke.fread.status.ui.threads

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.publish.PublishBlogStyle
import com.zhangke.fread.status.ui.style.StatusStyle

fun Modifier.threads(
    threadsType: ThreadsType,
    infoToTopSpacing: Float?,
    threadStyle: StatusStyle.ThreadsStyle,
    avatarSize: Dp,
    containerStartPadding: Dp,
    containerTopPadding: Dp,
): Modifier {
    if (threadsType == ThreadsType.NONE) return this
    infoToTopSpacing ?: return this
    val startMargin = containerStartPadding + avatarSize / 2
    return this.drawBehind {
        val topPaddingPx = containerTopPadding.toPx()
        val threadToAvatarSpacing = 2.dp.toPx()
        val containerTopPaddingPx = containerTopPadding.toPx()
        val strokeWidthPx = threadStyle.lineWidth.toPx()
        val startMarginPx = startMargin.toPx()
        if (threadsType.drawTopOfAvatarLine) {
            val needDrawTwoLine = infoToTopSpacing > containerTopPaddingPx * 2
            val firstLineBottom = if (needDrawTwoLine) {
                topPaddingPx / 2
            } else {
                topPaddingPx - threadToAvatarSpacing
            }
            drawLine(
                color = threadStyle.color,
                strokeWidth = strokeWidthPx,
                start = Offset(x = startMarginPx, y = 0F),
                end = Offset(
                    x = startMarginPx,
                    y = firstLineBottom,
                ),
                cap = StrokeCap.Round,
            )
            if (needDrawTwoLine) {
                // draw avatar-top to top-label-bottom line
                drawLine(
                    color = threadStyle.color,
                    strokeWidth = strokeWidthPx,
                    start = Offset(
                        x = startMarginPx,
                        y = infoToTopSpacing - topPaddingPx / 2
                    ),
                    end = Offset(x = startMarginPx, y = infoToTopSpacing - threadToAvatarSpacing),
                    cap = StrokeCap.Round,
                )
            }
        }
        if (threadsType.drawBottomOfAvatarLine) {
            val lineOnBottomOfAvatarY =
                infoToTopSpacing + avatarSize.toPx() + threadToAvatarSpacing
            drawLine(
                color = threadStyle.color,
                start = Offset(x = startMarginPx, y = lineOnBottomOfAvatarY),
                end = Offset(x = startMarginPx, y = size.height),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round,
            )
        }
    }
}

fun Modifier.threads(
    threadsType: ThreadsType,
    infoToTopSpacing: Float?,
    style: StatusStyle,
): Modifier {
    return this.threads(
        threadsType = threadsType,
        infoToTopSpacing = infoToTopSpacing,
        avatarSize = style.infoLineStyle.avatarSize,
        threadStyle = style.threadsStyle,
        containerStartPadding = style.containerStartPadding,
        containerTopPadding = style.containerTopPadding,
    )
}

fun Modifier.blogBeReplyThreads(
    threadsType: ThreadsType,
    publishBlogStyle: PublishBlogStyle,
): Modifier {
    return this.threads(
        threadsType = threadsType,
        infoToTopSpacing = 0F,
        threadStyle = publishBlogStyle.statusStyle.threadsStyle,
        containerTopPadding = 0.dp,
        containerStartPadding = publishBlogStyle.startPadding,
        avatarSize = publishBlogStyle.statusStyle.infoLineStyle.avatarSize,
    )
}

fun Modifier.blogInReplyingThreads(
    threadsType: ThreadsType,
    publishBlogStyle: PublishBlogStyle,
): Modifier {
    return this.threads(
        threadsType = threadsType,
        infoToTopSpacing = 0F,
        threadStyle = publishBlogStyle.statusStyle.threadsStyle,
        containerTopPadding = publishBlogStyle.topPadding,
        containerStartPadding = publishBlogStyle.startPadding,
        avatarSize = publishBlogStyle.statusStyle.infoLineStyle.avatarSize,
    )
}

internal val ThreadsType.drawTopOfAvatarLine: Boolean
    get() = this == ThreadsType.ANCHOR || this == ThreadsType.ANCESTOR

private val ThreadsType.drawBottomOfAvatarLine: Boolean
    get() = this == ThreadsType.FIRST_ANCESTOR || this == ThreadsType.ANCESTOR

internal val ThreadsType.contentIndent: Boolean
    get() = this == ThreadsType.FIRST_ANCESTOR || this == ThreadsType.ANCESTOR
