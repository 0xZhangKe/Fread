package com.zhangke.fread.status.ui.threads

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.zhangke.fread.status.ui.style.StatusStyle

fun Modifier.threads(
    threadsType: ThreadsType,
    containsTopLabel: Boolean,
    style: StatusStyle,
): Modifier {
    if (threadsType == ThreadsType.NONE) return this
    val threadsStyle = style.threadsStyle
    val startMargin = style.containerStartPadding + style.infoLineStyle.avatarSize / 2
    return this.drawBehind {
        val strokeWidthPx = threadsStyle.lineWidth.toPx()
        val startMarginPx = startMargin.toPx()
        if (threadsType.drawTopOfAvatarLine) {
            drawLine(
                color = threadsStyle.color,
                strokeWidth = strokeWidthPx,
                start = Offset(x = startMarginPx, y = 0F),
                end = Offset(x = startMarginPx, y = style.containerTopPadding.toPx()),
            )
//            if (containsTopLabel){
//                drawLine(
//                    color =  threadsStyle.color,
//                    strokeWidth = strokeWidthPx,
//                    start = Offset(x = startMarginPx, y = 0F),
//                    end = Offset(x = startMarginPx, y = style.containerTopPadding.toPx()),
//                )
//            }
        }
        if (threadsType.drawBottomOfAvatarLine) {
            val lineOnBottomOfAvatarY =
                style.infoLineStyle.avatarSize.toPx() + style.containerTopPadding.toPx()
            drawLine(
                color = style.threadsStyle.color,
                start = Offset(x = startMarginPx, y = lineOnBottomOfAvatarY),
                end = Offset(x = startMarginPx, y = size.height),
                strokeWidth = strokeWidthPx,
            )
        }
    }
}

private val ThreadsType.drawTopOfAvatarLine: Boolean
    get() = this == ThreadsType.ANCHOR || this == ThreadsType.ANCESTOR

private val ThreadsType.drawBottomOfAvatarLine: Boolean
    get() = this == ThreadsType.FIRST_ANCESTOR || this == ThreadsType.ANCESTOR

