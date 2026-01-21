package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.RoundedCornerCompat

@Composable
actual fun rememberDeviceCornerRadius(): Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    val insets = ViewCompat.getRootWindowInsets(view)
    val radiusPx =
        listOf(
            insets?.getRoundedCorner(RoundedCornerCompat.POSITION_TOP_LEFT),
            insets?.getRoundedCorner(RoundedCornerCompat.POSITION_TOP_RIGHT),
            insets?.getRoundedCorner(RoundedCornerCompat.POSITION_BOTTOM_RIGHT),
            insets?.getRoundedCorner(RoundedCornerCompat.POSITION_BOTTOM_LEFT),
        ).maxOfOrNull { it?.radius ?: 0 } ?: 0
    return if (radiusPx > 0) {
        with(density) { radiusPx.toDp() }
    } else {
        16.dp
    }
}
