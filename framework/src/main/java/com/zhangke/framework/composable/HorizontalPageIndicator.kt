package com.zhangke.framework.composable

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
        val radius = style.radius.toPx()
        val activeColor = style.activeColor
        val inactiveColor = style.inactiveColor
        val indicatorWidth = radius * 2
        val indicatorSpacing = radius * 2
        val totalWidth = pageCount * indicatorWidth + (pageCount - 1) * indicatorSpacing
        val startX = (size.width - totalWidth) / 2
        val startY = (size.height - radius * 2) / 2
        for (i in 0 until pageCount) {
            drawCircle(
                color = if (i == currentIndex) activeColor else inactiveColor,
                radius = radius,
                center = Offset(
                    x = startX + i * (indicatorWidth + indicatorSpacing) + radius,
                    y = startY + radius
                )
            )
        }
    }
}

data class IndicatorStyle(
    val radius: Dp,
    val activeColor: Color,
    val inactiveColor: Color,
) {

    companion object {

        @Composable
        fun default(
            radius: Dp = 4.dp,
            activeColor: Color = MaterialTheme.colorScheme.onSurface,
            inactiveColor: Color = MaterialTheme.colorScheme.primary,
        ): IndicatorStyle = IndicatorStyle(
            radius = radius,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
        )
    }
}
