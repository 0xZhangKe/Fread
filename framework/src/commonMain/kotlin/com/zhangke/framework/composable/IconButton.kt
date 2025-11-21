package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp

@Composable
fun SimpleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    iconSize: Dp,
    enabled: Boolean = true,
    imageVector: ImageVector,
    contentDescription: String?,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    tint: Color = LocalContentColor.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    Box(
        modifier = modifier
            .size(iconSize)
            .clip(CircleShape)
            .background(color = if (enabled) colors.containerColor else colors.disabledContainerColor)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Icon(
                modifier = iconModifier,
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = tint,
            )
        }
    }
}

@Composable
fun SimpleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    imageVector: ImageVector,
    contentDescription: String?,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    tint: Color? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    ) {
        val icTint = tint ?: LocalContentColor.current
        Icon(
            modifier = iconModifier,
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = icTint,
        )
    }
}

@Composable
fun SimpleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    painter: Painter,
    contentDescription: String?,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    tint: Color? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    ) {
        val icTint = tint ?: LocalContentColor.current
        Icon(
            modifier = iconModifier,
            painter = painter,
            contentDescription = contentDescription,
            tint = icTint,
        )
    }
}
