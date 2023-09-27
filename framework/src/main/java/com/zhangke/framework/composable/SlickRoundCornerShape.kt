package com.zhangke.framework.composable

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class SlickRoundCornerShape(
    private val topStart: CornerSize,
    private val topEnd: CornerSize,
    private val bottomEnd: CornerSize,
    private val bottomStart: CornerSize,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return createOutline(
            size = size,
            topStart = topStart.toPx(size, density),
            topEnd = topEnd.toPx(size, density),
            bottomEnd = bottomEnd.toPx(size, density),
            bottomStart = bottomStart.toPx(size, density),
            layoutDirection = layoutDirection
        )
    }

    private fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection,
    ): Outline {
        return if (topStart + topEnd + bottomEnd + bottomStart == 0.0f) {
            Outline.Rectangle(size.toRect())
        } else if (topStart == bottomStart && size.width < (topStart * 2F)) {
            val radius = topStart
            Outline.Generic(
                Path().apply {
                    moveTo(size.width, 0F)
                    arcTo(
                        rect = Rect(
                            left = 0F,
                            top = 0F,
                            right = radius * 2F,
                            bottom = radius * 2F,
                        ),
                        startAngleDegrees = 90F,
                        sweepAngleDegrees = 180F,
                        forceMoveTo = true,
                    )
                    close()
                }
            )
        } else {
            Outline.Rounded(
                RoundRect(
                    rect = size.toRect(),
                    topLeft = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) topStart else topEnd),
                    topRight = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) topEnd else topStart),
                    bottomRight = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) bottomEnd else bottomStart),
                    bottomLeft = CornerRadius(if (layoutDirection == LayoutDirection.Ltr) bottomStart else bottomEnd)
                )
            )
        }
    }

}

fun SlickRoundCornerShape(corner: CornerSize) =
    SlickRoundCornerShape(corner, corner, corner, corner)

fun SlickRoundCornerShape(size: Dp) =
    SlickRoundCornerShape(CornerSize(size))

fun SlickRoundCornerShape(size: Float) =
    SlickRoundCornerShape(CornerSize(size))

fun SlickRoundCornerShape(percent: Int) =
    SlickRoundCornerShape(CornerSize(percent))

fun SlickRoundCornerShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp
) = SlickRoundCornerShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart)
)

fun SlickRoundCornerShape(
    topStart: Float = 0.0f,
    topEnd: Float = 0.0f,
    bottomEnd: Float = 0.0f,
    bottomStart: Float = 0.0f
) = SlickRoundCornerShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart)
)

fun SlickRoundCornerShape(
    /*@IntRange(from = 0, to = 100)*/
    topStartPercent: Int = 0,
    /*@IntRange(from = 0, to = 100)*/
    topEndPercent: Int = 0,
    /*@IntRange(from = 0, to = 100)*/
    bottomEndPercent: Int = 0,
    /*@IntRange(from = 0, to = 100)*/
    bottomStartPercent: Int = 0
) = SlickRoundCornerShape(
    topStart = CornerSize(topStartPercent),
    topEnd = CornerSize(topEndPercent),
    bottomEnd = CornerSize(bottomEndPercent),
    bottomStart = CornerSize(bottomStartPercent)
)
