package com.zhangke.framework.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun Modifier.noRippleClick(enabled: Boolean = true, onClick: () -> Unit): Modifier {
    return Modifier.clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick,
        indication = null,
    ) then this
}
