package com.zhangke.fread.status.ui.threads

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.style.StatusStyle

fun Modifier.threads(
    threadsType: ThreadsType,
    infoToTopSpacing: Float?,
    style: StatusStyle,
): Modifier {
    if (threadsType == ThreadsType.NONE) return this
    infoToTopSpacing ?: return this
    val threadsStyle = style.threadsStyle
    val startMargin = style.containerStartPadding + style.infoLineStyle.avatarSize / 2
    return this.drawBehind {
        val topPaddingPx = style.containerTopPadding.toPx()
        val threadToAvatarSpacing = 2.dp.toPx()
        val containerTopPaddingPx = style.containerTopPadding.toPx()
        val strokeWidthPx = threadsStyle.lineWidth.toPx()
        val startMarginPx = startMargin.toPx()
        if (threadsType.drawTopOfAvatarLine) {
            val needDrawTwoLine = infoToTopSpacing > containerTopPaddingPx * 2
            val firstLineBottom = if (needDrawTwoLine) {
                topPaddingPx / 2
            } else {
                topPaddingPx - threadToAvatarSpacing
            }
            drawLine(
                color = threadsStyle.color,
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
                    color = threadsStyle.color,
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
                infoToTopSpacing + style.infoLineStyle.avatarSize.toPx() + threadToAvatarSpacing
            drawLine(
                color = style.threadsStyle.color,
                start = Offset(x = startMarginPx, y = lineOnBottomOfAvatarY),
                end = Offset(x = startMarginPx, y = size.height),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round,
            )
        }
    }
}

internal val ThreadsType.drawTopOfAvatarLine: Boolean
    get() = this == ThreadsType.ANCHOR || this == ThreadsType.ANCESTOR

private val ThreadsType.drawBottomOfAvatarLine: Boolean
    get() = this == ThreadsType.FIRST_ANCESTOR || this == ThreadsType.ANCESTOR

internal val ThreadsType.contentIndent: Boolean
    get() = this == ThreadsType.FIRST_ANCESTOR || this == ThreadsType.ANCESTOR
