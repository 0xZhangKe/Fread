package com.zhangke.framework.composable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StyledTextButton(
    modifier: Modifier,
    text: String,
    style: TextButtonStyle,
    onClick: () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
) {
    val containerColor: Color
    val textColor: Color
    when (style) {
        TextButtonStyle.DISABLE -> {
            // in disable style, btn will use disable-container-color, it's transparent.
            containerColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onSurface
        }

        TextButtonStyle.ALERT -> {
            containerColor = MaterialTheme.colorScheme.errorContainer
            textColor = MaterialTheme.colorScheme.onErrorContainer
        }

        TextButtonStyle.STANDARD -> {
            containerColor = MaterialTheme.colorScheme.primaryContainer
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        }

        TextButtonStyle.ACTIVE -> {
            containerColor = MaterialTheme.colorScheme.primary
            textColor = MaterialTheme.colorScheme.onPrimary
        }
    }
    TextButton(
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
        ),
        enabled = style != TextButtonStyle.DISABLE,
        onClick = onClick,
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            color = textColor,
        )
    }
}

enum class TextButtonStyle {

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
