package com.zhangke.framework.composable

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun StyledIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    style: IconButtonStyle,
    onClick: () -> Unit,
    contentDescription: String? = null,
) {
    val containerColor: Color
    val contentColor: Color
    when (style) {
        IconButtonStyle.DISABLE -> {
            // in disable style, btn will use disable-container-color, it's transparent.
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onSurface
        }

        IconButtonStyle.ALERT -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        }

        IconButtonStyle.STANDARD -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        }

        IconButtonStyle.ACTIVE -> {
            containerColor = MaterialTheme.colorScheme.primary
            contentColor = MaterialTheme.colorScheme.onPrimary
        }
    }
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
        )
    }
}

enum class IconButtonStyle {

    /**
     * 禁用状态
     */
    DISABLE,

    /**
     * 警告状态，略微负面。
     */
    ALERT,

    /**
     * 普通状态，中性。
     */
    STANDARD,

    /**
     * 活跃状态，正面，希望被点击。
     */
    ACTIVE,
}
