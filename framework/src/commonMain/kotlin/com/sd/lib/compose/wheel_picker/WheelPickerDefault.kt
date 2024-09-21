package com.sd.lib.compose.wheel_picker


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The default implementation of focus view in vertical.
 */
@Composable
fun FWheelPickerFocusVertical(
    modifier: Modifier = Modifier,
    dividerSize: Dp = 1.dp,
    dividerColor: Color = DefaultDividerColor,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(dividerColor)
                .height(dividerSize)
                .fillMaxWidth()
                .align(Alignment.TopCenter),
        )
        Box(
            modifier = Modifier
                .background(dividerColor)
                .height(dividerSize)
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
        )
    }
}

/**
 * The default implementation of focus view in horizontal.
 */
@Composable
fun FWheelPickerFocusHorizontal(
    modifier: Modifier = Modifier,
    dividerSize: Dp = 1.dp,
    dividerColor: Color = DefaultDividerColor,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .background(dividerColor)
                .width(dividerSize)
                .fillMaxHeight()
                .align(Alignment.CenterStart),
        )
        Box(
            modifier = Modifier
                .background(dividerColor)
                .width(dividerSize)
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
        )
    }
}

/**
 * Default divider color.
 */
private val DefaultDividerColor: Color
    @Composable
    get() {
        val color = if (isSystemInDarkTheme()) Color.White else Color.Black
        return color.copy(alpha = 0.2f)
    }

/**
 * Default display.
 */
@Composable
fun FWheelPickerDisplayScope.DefaultWheelPickerDisplay(
    index: Int,
) {
    val focused = index == state.currentIndexSnapshot
    val targetScale = if (focused) 1.0f else 0.8f
    val animateScale by animateFloatAsState(targetScale, label = "")
    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = if (focused) 1.0f else 0.3f
            this.scaleX = animateScale
            this.scaleY = animateScale
        }
    ) {
        Content(index)
    }
}
