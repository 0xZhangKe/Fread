package com.zhangke.framework.composable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.blur.blurEffectContainerColor
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = blurEffectContainerColor(containerColor = containerColor),
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        modifier = modifier.applyBlurEffect(
            containerColor = containerColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .padding(top = 16.dp, bottom = 8.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val styledIcon = @Composable {
        val iconColor by colors.iconColor(selected = selected, enabled = enabled)
        // If there's a label, don't have a11y services repeat the icon description.
        val clearSemantics = label != null && (alwaysShowLabel || selected)
        Box(modifier = if (clearSemantics) Modifier.clearAndSetSemantics {} else Modifier) {
            CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
        }
    }

    var itemWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .weight(1f)
            .onSizeChanged {
                itemWidth = it.width
            },
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        val animationProgress: State<Float> = animateFloatAsState(
            targetValue = if (selected) 1f else 0f,
            animationSpec = tween(ItemAnimationDurationMillis),
            label = "navigation-bar",
        )

        val deltaOffset: Offset
        with(LocalDensity.current) {
            val indicatorWidth = 64.dp.roundToPx()
            deltaOffset = Offset(
                (itemWidth - indicatorWidth).toFloat() / 2,
                IndicatorVerticalOffset.toPx()
            )
        }
        val offsetInteractionSource = remember(interactionSource, deltaOffset) {
            MappedInteractionSource(interactionSource, deltaOffset)
        }

        val indicatorRipple = @Composable {
            @Suppress("DEPRECATION_ERROR")
            (Box(
                Modifier
                    .layoutId(IndicatorRippleLayoutIdTag)
                    .clip(CircleShape)
                    .indication(
                        offsetInteractionSource,
                        ripple()
                    )
            ))
        }
        val indicator = @Composable {
            Box(
                Modifier
                    .layoutId(IndicatorLayoutIdTag)
                    .graphicsLayer { alpha = animationProgress.value }
                    .background(
                        color = colors.selectedIndicatorColor,
                        shape = CircleShape,
                    )
            )
        }

        NavigationBarItemLayout(
            indicatorRipple = indicatorRipple,
            indicator = indicator,
            icon = styledIcon,
            animationProgress = { animationProgress.value },
        )
    }
}

@Composable
private fun NavigationBarItemColors.iconColor(selected: Boolean, enabled: Boolean): State<Color> {
    val targetValue = when {
        !enabled -> disabledIconColor
        selected -> selectedIconColor
        else -> unselectedIconColor
    }
    return animateColorAsState(
        targetValue = targetValue,
        animationSpec = tween(ItemAnimationDurationMillis),
        label = "BottomNavIconColorAnimation",
    )
}

object NavigationBarItemDefaults {

    @Composable
    fun colors() = MaterialTheme.colorScheme.defaultNavigationBarItemColors

    @Composable
    fun colors(
        selectedIconColor: Color = Color.Unspecified,
        selectedTextColor: Color = Color.Unspecified,
        indicatorColor: Color = Color.Unspecified,
        unselectedIconColor: Color = Color.Unspecified,
        unselectedTextColor: Color = Color.Unspecified,
        disabledIconColor: Color = Color.Unspecified,
        disabledTextColor: Color = Color.Unspecified,
    ): NavigationBarItemColors = MaterialTheme.colorScheme.defaultNavigationBarItemColors.copy(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        selectedIndicatorColor = indicatorColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor,
    )

    private const val DisabledAlpha = 0.38f

    private val ColorScheme.defaultNavigationBarItemColors: NavigationBarItemColors
        @Composable
        get() {
            return NavigationBarItemColors(
                selectedIconColor = onSecondaryContainer,
                selectedTextColor = onSurface,
                selectedIndicatorColor = secondaryContainer,
                unselectedIconColor = onSurfaceVariant,
                unselectedTextColor = onSurfaceVariant,
                disabledIconColor = onSurfaceVariant.copy(alpha = DisabledAlpha),
                disabledTextColor = onSurfaceVariant.copy(alpha = DisabledAlpha),
            )
        }
}

@Composable
private fun NavigationBarItemLayout(
    indicatorRipple: @Composable () -> Unit,
    indicator: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    animationProgress: () -> Float,
) {
    Layout(
        content = {
            indicatorRipple()
            indicator()
            Box(Modifier.layoutId(IconLayoutIdTag)) { icon() }
        },
        measurePolicy = { measurables, constraints ->
            @Suppress("NAME_SHADOWING")
            val animationProgress = animationProgress()
            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
            val iconPlaceable =
                measurables.fastFirst { it.layoutId == IconLayoutIdTag }.measure(looseConstraints)

            val totalIndicatorWidth =
                iconPlaceable.width + (IndicatorHorizontalPadding * 2).roundToPx()
            val animatedIndicatorWidth = (totalIndicatorWidth * animationProgress).roundToInt()
            val indicatorHeight = iconPlaceable.height + (IndicatorVerticalPadding * 2).roundToPx()
            val indicatorRipplePlaceable =
                measurables
                    .fastFirst { it.layoutId == IndicatorRippleLayoutIdTag }
                    .measure(
                        Constraints.fixed(
                            width = totalIndicatorWidth,
                            height = indicatorHeight
                        )
                    )
            val indicatorPlaceable =
                measurables
                    .fastFirstOrNull { it.layoutId == IndicatorLayoutIdTag }
                    ?.measure(
                        Constraints.fixed(
                            width = animatedIndicatorWidth,
                            height = indicatorHeight
                        )
                    )
            placeIcon(iconPlaceable, indicatorRipplePlaceable, indicatorPlaceable, constraints)
        }
    )
}

/**
 * Places the provided [Placeable]s in the center of the provided [constraints].
 */
private fun MeasureScope.placeIcon(
    iconPlaceable: Placeable,
    indicatorRipplePlaceable: Placeable,
    indicatorPlaceable: Placeable?,
    constraints: Constraints
): MeasureResult {
    val width = constraints.maxWidth
    val height = constraints.constrainHeight(32.dp.roundToPx())

    val iconX = (width - iconPlaceable.width) / 2
    val iconY = (height - iconPlaceable.height) / 2

    val rippleX = (width - indicatorRipplePlaceable.width) / 2
    val rippleY = (height - indicatorRipplePlaceable.height) / 2

    return layout(width, height) {
        indicatorPlaceable?.let {
            val indicatorX = (width - it.width) / 2
            val indicatorY = (height - it.height) / 2
            it.placeRelative(indicatorX, indicatorY)
        }
        iconPlaceable.placeRelative(iconX, iconY)
        indicatorRipplePlaceable.placeRelative(rippleX, rippleY)
    }
}

private class MappedInteractionSource(
    underlyingInteractionSource: InteractionSource,
    private val delta: Offset
) : InteractionSource {

    private val mappedPresses = mutableMapOf<PressInteraction.Press, PressInteraction.Press>()

    override val interactions = underlyingInteractionSource.interactions.map { interaction ->
        when (interaction) {
            is PressInteraction.Press -> {
                val mappedPress = mapPress(interaction)
                mappedPresses[interaction] = mappedPress
                mappedPress
            }

            is PressInteraction.Cancel -> {
                val mappedPress = mappedPresses.remove(interaction.press)
                if (mappedPress == null) {
                    interaction
                } else {
                    PressInteraction.Cancel(mappedPress)
                }
            }

            is PressInteraction.Release -> {
                val mappedPress = mappedPresses.remove(interaction.press)
                if (mappedPress == null) {
                    interaction
                } else {
                    PressInteraction.Release(mappedPress)
                }
            }

            else -> interaction
        }
    }

    private fun mapPress(press: PressInteraction.Press): PressInteraction.Press =
        PressInteraction.Press(press.pressPosition - delta)
}

private const val IndicatorRippleLayoutIdTag: String = "indicatorRipple"

private const val IndicatorLayoutIdTag: String = "indicator"

private const val IconLayoutIdTag: String = "icon"

private const val ItemAnimationDurationMillis: Int = 100

private val IndicatorHorizontalPadding: Dp =
    (64.dp - 24.dp) / 2

private val IndicatorVerticalPadding: Dp =
    (32.dp - 24.dp) / 2

private val IndicatorVerticalOffset: Dp = 12.dp
