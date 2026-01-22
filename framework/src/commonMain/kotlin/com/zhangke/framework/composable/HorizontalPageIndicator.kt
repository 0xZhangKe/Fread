package com.zhangke.framework.composable

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalPageIndicator(
    currentIndex: Int,
    pageCount: Int,
    modifier: Modifier,
    style: IndicatorStyle = IndicatorStyle.default(),
) {
    Canvas(modifier = modifier) {
        val inactiveDiameter = style.inactiveDiameter.toPx()
        val inactiveRadius = inactiveDiameter / 2
        val activeLineWidth = style.activeLineWidth.toPx()
        val activeColor = style.activeColor
        val inactiveColor = style.inactiveColor
        val indicatorSpacing = inactiveDiameter
        var totalWidth = 0f
        for (i in 0 until pageCount) {
            totalWidth += if (i == currentIndex) activeLineWidth else inactiveDiameter
        }
        totalWidth += (pageCount - 1) * indicatorSpacing
        val startX = (size.width - totalWidth) / 2
        val startY = (size.height - inactiveDiameter) / 2
        var currentX = startX
        for (i in 0 until pageCount) {
            if (i == currentIndex) {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(currentX, startY),
                    size = Size(activeLineWidth, inactiveDiameter),
                    cornerRadius = CornerRadius(inactiveRadius, inactiveRadius),
                )
                currentX += activeLineWidth + indicatorSpacing
            } else {
                drawCircle(
                    color = inactiveColor,
                    radius = inactiveRadius,
                    center = Offset(
                        x = currentX + inactiveRadius,
                        y = startY + inactiveRadius
                    )
                )
                currentX += inactiveDiameter + indicatorSpacing
            }
        }
    }
}

data class IndicatorStyle(
    val activeLineWidth: Dp,
    val inactiveDiameter: Dp,
    val activeColor: Color,
    val inactiveColor: Color,
) {

    companion object {

        @Composable
        fun default(
            activeLineWidth: Dp = 12.dp,
            inactiveDiameter: Dp = 4.dp,
            activeColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
            inactiveColor: Color = MaterialTheme.colorScheme.inverseOnSurface,
        ): IndicatorStyle = IndicatorStyle(
            activeLineWidth = activeLineWidth,
            inactiveDiameter = inactiveDiameter,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
        )
    }
}
