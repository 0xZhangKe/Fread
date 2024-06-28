package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.status.model.StatusUiInteraction

@Composable
fun StatusBottomInteractionPanel(
    modifier: Modifier = Modifier,
    interactions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
) {
    if (interactions.isEmpty()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        interactions.forEachIndexed { index, interaction ->
            val weight = when (index) {
                0 -> 1F
                interactions.lastIndex -> 1F
                else -> 2F
            }
            Box(modifier = Modifier.weight(weight)) {
                val alignment = when (index) {
                    0 -> Alignment.CenterStart
                    interactions.lastIndex -> Alignment.CenterEnd
                    else -> Alignment.Center
                }
                StatusActionIcon(
                    modifier = Modifier.align(alignment),
                    imageVector = interaction.logo,
                    enabled = interaction.enabled,
                    contentDescription = interaction.actionName,
                    text = interaction.label,
                    highLight = interaction.highLight,
                    onClick = {
                        onInteractive(interaction)
                    },
                )
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
    text: String? = null,
    highLight: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
    ) {
        Row(
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
