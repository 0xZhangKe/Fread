package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun StatusBottomInteractionPanel(
    modifier: Modifier = Modifier,
    style: StatusStyle,
    interactions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    if (interactions.isEmpty()) return
    val startPadding = style.containerStartPadding / 2 + style.bottomPanelStyle.startPadding
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startPadding, end = style.containerEndPadding / 2),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        interactions.forEachIndexed { index, interaction ->
            StatusActionIcon(
                modifier = Modifier,
                imageVector = interaction.logo,
                enabled = interaction.enabled,
                style = style,
                contentDescription = interaction.actionName,
                text = interaction.label,
                highLight = interaction.highLight,
                onClick = {
                    onInteractive(interaction)
                },
            )
            if (index != interactions.lastIndex) {
                Spacer(modifier = Modifier.weight(1F))
            }
        }
    }
}

@Composable
private fun StatusActionIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    enabled: Boolean,
    contentDescription: String,
    style: StatusStyle,
    text: String? = null,
    highLight: Boolean,
    onClick: () -> Unit,
) {
    StatusIconButton(
        modifier = modifier.height(style.bottomPanelStyle.iconSize),
        onClick = onClick,
        enabled = enabled,
    ) {
        Row(
            modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val contentColor = if (highLight) {
                MaterialTheme.colorScheme.tertiary
            } else {
                LocalContentColor.current
            }
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = imageVector,
                contentDescription = contentDescription,
                tint = contentColor,
            )
            if (text != null) {
                Text(
                    modifier = Modifier.padding(start = 2.dp),
                    text = text,
                    maxLines = 1,
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun StatusIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .background(color = if (enabled) colors.containerColor else colors.disabledContainerColor)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = ripple(
                    bounded = false,
                    radius = 20.dp,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}
