package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            StatusActionIcon(
                modifier = Modifier,
                imageVector = interaction.logo,
                enabled = interaction.enabled,
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
    text: String? = null,
    highLight: Boolean,
    onClick: () -> Unit,
) {
    val iconContentColor = LocalContentColor.current
    TextButton(
        modifier = modifier.height(40.dp),
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = iconContentColor,
            disabledContentColor = iconContentColor.copy(alpha = 0.38F)
        ),
        contentPadding = PaddingValues(horizontal = 12.dp),
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
