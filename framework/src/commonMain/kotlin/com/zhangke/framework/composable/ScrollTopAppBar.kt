package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.SubcomposeLayout
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.blur.blurEffectContainerColor
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollTopAppBar(
    modifier: Modifier,
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    colors: ScrollTopAppBarColors = ScrollTopAppBarColors.default(),
    navigationIcon: @Composable () -> Unit = {},
) {
    val appBarContainerColor by remember(scrollBehavior, colors) {
        derivedStateOf {
            val fraction = scrollBehavior.state.overlappedFraction.coerceIn(0F, 1F)
            lerp(colors.containerColor, colors.scrolledContainerColor, fraction)
        }
    }
    SubcomposeLayout(
        modifier = modifier.background(appBarContainerColor),
    ) { constraints ->
        val topAppBarPlaceable = subcompose("topAppBar") {
            SingleRowTopAppBar(
                modifier = modifier.applyBlurEffect(containerColor = appBarContainerColor),
                navigationIcon = navigationIcon,
                title = title,
                actions = actions,
                colors = TopAppBarColors.default(
                    containerColor = blurEffectContainerColor(containerColor = appBarContainerColor),
                    navigationIconContentColor = colors.contentColor,
                    titleContentColor = colors.contentColor,
                    actionIconContentColor = colors.contentColor,
                ),
            )
        }.first().measure(constraints)
        val totalHeight = topAppBarPlaceable.height
        if (scrollBehavior.state.heightOffsetLimit != -totalHeight.toFloat()) {
            scrollBehavior.state.heightOffsetLimit = -totalHeight.toFloat()
        }
        val heightOffset = scrollBehavior.state.heightOffset
        val layoutHeight = (totalHeight + heightOffset).roundToInt().coerceAtLeast(0)
        layout(constraints.maxWidth, layoutHeight) {
            val topAppBarY = heightOffset.roundToInt()
            topAppBarPlaceable.placeRelative(0, topAppBarY)
        }
    }
}

data class ScrollTopAppBarColors(
    val containerColor: Color,
    val scrolledContainerColor: Color,
    val contentColor: Color,
) {

    companion object {

        @Composable
        fun default(
            containerColor: Color = MaterialTheme.colorScheme.surface,
            scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
            contentColor: Color = MaterialTheme.colorScheme.onSurface,
        ): ScrollTopAppBarColors {
            return ScrollTopAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = scrolledContainerColor,
                contentColor = contentColor,
            )
        }
    }
}
